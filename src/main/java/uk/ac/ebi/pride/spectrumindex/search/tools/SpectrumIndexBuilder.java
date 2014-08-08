package uk.ac.ebi.pride.spectrumindex.search.tools;

import org.apache.solr.client.solrj.SolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import uk.ac.ebi.pride.archive.dataprovider.assay.AssayProvider;
import uk.ac.ebi.pride.archive.dataprovider.file.ProjectFileSource;
import uk.ac.ebi.pride.archive.dataprovider.project.ProjectProvider;
import uk.ac.ebi.pride.archive.repo.assay.AssayRepository;
import uk.ac.ebi.pride.archive.repo.file.ProjectFileRepository;
import uk.ac.ebi.pride.archive.repo.project.ProjectRepository;
import uk.ac.ebi.pride.spectrumindex.search.model.Spectrum;
import uk.ac.ebi.pride.spectrumindex.search.service.SpectrumIndexService;
import uk.ac.ebi.pride.spectrumindex.search.service.SpectrumSearchService;
import uk.ac.ebi.pride.spectrumindex.search.indexer.ProjectSpectraIndexer;

import java.io.File;
import java.util.Calendar;
import java.util.List;


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
    private SolrServer spectrumSolrServer;

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

        ApplicationContext context = new ClassPathXmlApplicationContext("spring/app-context.xml");

        SpectrumIndexBuilder spectrumIndexBuilder = context.getBean(SpectrumIndexBuilder.class);

        if (args.length>0 && "inc".equals(args[0].toLowerCase())) {
            logger.info("Starting incremental indexing process...");
            indexNonExistingSpectra(spectrumIndexBuilder);
            logger.info("DONE!");
        } else if (args.length>0 && "all".equals(args[0].toLowerCase())) {
            logger.info("Starting from-scratch indexing process...");
            indexSpectra(spectrumIndexBuilder);
            logger.info("DONE!");
        } else {
            System.out.println("Arguments:");
            System.out.println("   inc   - index spectra not already in the index");
            System.out.println("   all   - deletes the index and index all spectra");
        }


    }


    public static void indexSpectra(SpectrumIndexBuilder spectrumIndexBuilder) {

        logger.info("Getting all projects in DB...");
        // get all projects on repository
        Iterable<? extends ProjectProvider> projects = spectrumIndexBuilder.projectRepository.findAll();
        logger.info("DONE!");

        // reset index
        logger.info("Deleting all spectra from index...");
        spectrumIndexBuilder.spectrumIndexService.deleteAll();
        logger.info("DONE!");

        // create the indexer
        ProjectSpectraIndexer projectSpectraIndexer = new ProjectSpectraIndexer(spectrumIndexBuilder.spectrumSearchService, spectrumIndexBuilder.spectrumIndexService);

        // iterate through project to index spectra
        logger.info("Indexing spectra...");
        for (ProjectProvider project : projects) {
            logger.info("Indexing spectra for project " + project.getAccession() + "...");
            indexSpectraForProject(project, spectrumIndexBuilder, projectSpectraIndexer);
            logger.info("DONE indexing project " + project.getAccession() + "!");
        }
        logger.info("DONE indexing all spectra!");
    }

    public static void indexNonExistingSpectra(SpectrumIndexBuilder spectrumIndexBuilder) {

        logger.info("Getting all projects in DB...");
        // get all projects on repository
        Iterable<? extends ProjectProvider> projects = spectrumIndexBuilder.projectRepository.findAll();
        logger.info("DONE!");

        // create the indexer
        ProjectSpectraIndexer projectSpectraIndexer = new ProjectSpectraIndexer(spectrumIndexBuilder.spectrumSearchService, spectrumIndexBuilder.spectrumIndexService);

        // iterate through project to index spectra
        logger.info("Indexing spectra...");
        for (ProjectProvider project : projects) {
            logger.info("Indexing spectra for project " + project.getAccession() + "...");
            indexNonExistingSpectraForProject(project, spectrumIndexBuilder, projectSpectraIndexer);
            logger.info("DONE indexing project " + project.getAccession() + "!");
        }
        logger.info("DONE indexing all spectra!");
    }


    private static void indexNonExistingSpectraForProject(ProjectProvider project, SpectrumIndexBuilder spectrumIndexBuilder, ProjectSpectraIndexer projectSpectraIndexer) {

        // made up an MGF file name per assay (temporary)
        Iterable<? extends AssayProvider> projectAssays = spectrumIndexBuilder.assayRepository.findAllByProjectId(project.getId());
        for (AssayProvider assay: projectAssays) {
            List<Spectrum> assaySpectra = spectrumIndexBuilder.spectrumSearchService.findByAssayAccession(assay.getAccession(), new PageRequest(0,1)).getContent();

            if (assaySpectra!=null && assaySpectra.size()>0) {
                logger.info("Assay " + assay.getAccession() + " of Project " + project.getAccession() + " already in the index!");
            } else {
                String mgfFileName = "PRIDE_Exp_Complete_Ac_" + assay.getAccession() + MGF_FILE_EXTENSION;
                String pathToMgfFile = buildAbsoluteMgfFilePath(
                        spectrumIndexBuilder.submissionsDirectory.getAbsolutePath(),
                        project,
                        mgfFileName
                );

                File mgfFile = new File(pathToMgfFile);
                if (mgfFile.exists()) {
                    logger.info("Indexing spectra for file " + pathToMgfFile + "...");
                    projectSpectraIndexer.indexAllSpectraForProjectAndAssay(project.getAccession(), assay.getAccession(), mgfFile);
                    logger.info("DONE indexing file " + pathToMgfFile + "!");
                } else {
                    logger.info("File " + pathToMgfFile + " does not exists");
                }
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
    }


    private static void indexSpectraForProject(ProjectProvider project, SpectrumIndexBuilder spectrumIndexBuilder, ProjectSpectraIndexer projectSpectraIndexer) {

        // made up an MGF file name per assay (temporary)
        Iterable<? extends AssayProvider> projectAssays = spectrumIndexBuilder.assayRepository.findAllByProjectId(project.getId());
        for (AssayProvider assay: projectAssays) {

            String mgfFileName = "PRIDE_Exp_Complete_Ac_" + assay.getAccession() + MGF_FILE_EXTENSION;
            String pathToMgfFile = buildAbsoluteMgfFilePath(
                    spectrumIndexBuilder.submissionsDirectory.getAbsolutePath(),
                    project,
                    mgfFileName
            );

            File mgfFile = new File(pathToMgfFile);
            if (mgfFile.exists()) {
                logger.info("Indexing spectra for file " + pathToMgfFile + "...");
                projectSpectraIndexer.indexAllSpectraForProjectAndAssay(project.getAccession(), assay.getAccession(), mgfFile);
                logger.info("DONE indexing file " + pathToMgfFile + "!");
            } else {
                logger.info("File " + pathToMgfFile + " does not exists");
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
