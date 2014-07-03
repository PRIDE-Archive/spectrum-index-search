package uk.ac.ebi.pride.spectrumindex.search.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import uk.ac.ebi.pride.archive.dataprovider.assay.AssayProvider;
import uk.ac.ebi.pride.archive.dataprovider.file.ProjectFileProvider;
import uk.ac.ebi.pride.archive.dataprovider.file.ProjectFileSource;
import uk.ac.ebi.pride.archive.dataprovider.project.ProjectProvider;
import uk.ac.ebi.pride.archive.repo.assay.AssayRepository;
import uk.ac.ebi.pride.archive.repo.file.ProjectFileRepository;
import uk.ac.ebi.pride.archive.repo.project.ProjectRepository;
import uk.ac.ebi.pride.spectrumindex.search.service.SpectrumIndexService;
import uk.ac.ebi.pride.spectrumindex.search.service.SpectrumSearchService;
import uk.ac.ebi.pride.spectrumindex.search.indexer.ProjectSpectraIndexer;

import java.io.File;
import java.util.Calendar;


/**
 * @author Jose A. Dianes
 * @version $Id$
 */
@Component
public class SpectrumIndexBuilder {

    private static final String MGF_FILE_EXTENSION = ".pride.mgf";
    private static Logger logger = LoggerFactory.getLogger(SpectrumIndexBuilder.class.getName());

    private static final String COMPRESS_EXTENSION = "gz";

    private static final String INTERNAL_FOLDER_NAME = ProjectFileSource.INTERNAL.getFolderName();
    private static final String GENERATED_FOLDER_NAME = ProjectFileSource.GENERATED.getFolderName();

    @Autowired
    private File submissionsDirectory;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private AssayRepository assayRepository;

    @Autowired
    private ProjectFileRepository projectFileRepository;

    @Autowired
    private SpectrumSearchService spectrumSearchService;

    @Autowired
    private SpectrumIndexService spectrumIndexService;


    public static void main(String[] args) {

        System.out.print("Creating application context...");
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/app-context.xml");
        System.out.println("DONE!");

        System.out.print("Creating index builder...");
        SpectrumIndexBuilder spectrumIndexBuilder = context.getBean(SpectrumIndexBuilder.class);
        System.out.println("DONE!");

        System.out.print("Starting indexing process...");
        indexSpectra(spectrumIndexBuilder);
        System.out.println("DONE!");
    }


    public static void indexSpectra(SpectrumIndexBuilder spectrumIndexBuilder) {

        System.out.print("Getting all projects in DB...");
        // get all projects on repository
        Iterable<? extends ProjectProvider> projects = spectrumIndexBuilder.projectRepository.findAll();
        System.out.println("DONE!");

        // reset index
        System.out.print("Deleting all spectra from index...");
        spectrumIndexBuilder.spectrumIndexService.deleteAll();
        System.out.println("DONE!");

        // create the indexer
        ProjectSpectraIndexer projectSpectraIndexer = new ProjectSpectraIndexer(spectrumIndexBuilder.spectrumSearchService, spectrumIndexBuilder.spectrumIndexService);

        // iterate through project to index spectra
        System.out.println("Indexing spectra...");
        for (ProjectProvider project : projects) {
            System.out.println("Indexing spectra for project " + project.getAccession() + "...");

            // made up an MGF file name per assay (temporary)
            Iterable<? extends AssayProvider> projectAssays = spectrumIndexBuilder.assayRepository.findAllByProjectId(project.getId());
            for (AssayProvider assay: projectAssays) {
                String mgfFileName = "PRIDE_Exp_Complete_Ac_" + assay.getAccession() + ".pride.mgf";
                String pathToMgfFile = buildAbsoluteMgfFilePath(
                        spectrumIndexBuilder.submissionsDirectory.getAbsolutePath(),
                        project,
                        mgfFileName
                );

                File mgfFile = new File(pathToMgfFile);
                if (mgfFile.exists()) {
                    System.out.println("Indexing spectra for file " + pathToMgfFile + "...");
                    projectSpectraIndexer.indexAllSpectraForProjectAndAssay(project.getAccession(), assay.getAccession(), mgfFile);
                    System.out.println("DONE indexing file " + pathToMgfFile + "!");
                } else {
                    System.out.println("File " + pathToMgfFile + " does not exists");
                }

            }
//            Iterable<? extends ProjectFileProvider> projectFiles = spectrumIndexBuilder.projectFileRepository.findAllByProjectId(project.getId());
//            for (ProjectFileProvider projectFile : projectFiles) {
//                //TODO: This will change when we have the internal file names in the database
//                System.out.println("Checking file " + projectFile.getFileName() + "...");
//
//
//                if (ProjectFileSource.GENERATED.equals(projectFile.getFileSource())) {
//
//                    String fileName = projectFile.getFileName();
//
//                    if (fileName != null && fileName.contains(MGF_FILE_EXTENSION)) {
//                        String pathToMgfFile = buildAbsoluteMgfFilePath(
//                                spectrumIndexBuilder.submissionsDirectory.getAbsolutePath(),
//                                project,
//                                fileName
//                        );
//
//                        String assayAccession = spectrumIndexBuilder.assayRepository.findOne(projectFile.getAssayId()).getAccession()
//                        System.out.print("Indexing spectra for file " + pathToMgfFile + "...");
//                        projectSpectraIndexer.indexAllSpectraForProjectAndAssay(project.getAccession(), assayAccession, new File(pathToMgfFile));
//                        System.out.println("DONE indexing file " + pathToMgfFile + "!");
//
//                    }
//
//                }
//            }
            System.out.println("DONE indexing project " + project.getAccession() + "!");
        }
        System.out.println("DONE indexing all spectra!");
    }


    //TODO: Move it to a pride-archive-utils
    public static String buildAbsoluteMgfFilePath(String prefix, ProjectProvider project, String fileName) {
        if (project.isPublicProject()) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(project.getPublicationDate());
            int month = calendar.get(Calendar.MONTH) + 1;

            return prefix
                    + File.separator + calendar.get(Calendar.YEAR)
                    + File.separator + (month < 10 ? "0" : "") + month
                    + File.separator + project.getAccession()
                    + File.separator + GENERATED_FOLDER_NAME
                    + File.separator + translateFromGeneratedToInternalFolderFileName(fileName);
        } else {
            return prefix
                    + File.separator + project.getAccession()
                    + File.separator + GENERATED_FOLDER_NAME
                    + File.separator + translateFromGeneratedToInternalFolderFileName(fileName);
        }

    }

    //TODO: Move it to a pride-archive-utils
    /**
     * In the generated folder(the which one we are taking the file names) the files are gzip, so we need to remove
     * the extension to have the name in the internal folder (the one that we want)
     *
     * @param fileName mztab file name in generated folder
     * @return mztab file name in internal folder
     */
    private static String translateFromGeneratedToInternalFolderFileName(String fileName) {

        if (fileName != null) {
            if (fileName.endsWith(COMPRESS_EXTENSION)) {
                fileName = fileName.substring(0, fileName.lastIndexOf("."));
            }
        }
        return fileName;
    }
}
