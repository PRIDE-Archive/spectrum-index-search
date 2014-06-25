package uk.ac.ebi.pride.spectrumindex.search.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
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

        ApplicationContext context = new ClassPathXmlApplicationContext("spring/app-context.xml");
        SpectrumIndexBuilder spectrumIndexBuilder = context.getBean(SpectrumIndexBuilder.class);

        indexSpectra(spectrumIndexBuilder);

    }


    public static void indexSpectra(SpectrumIndexBuilder spectrumIndexBuilder) {

        // get all projects on repository
        Iterable<? extends ProjectProvider> projects = spectrumIndexBuilder.projectRepository.findAll();

        // reset index
        spectrumIndexBuilder.spectrumIndexService.deleteAll();
        logger.info("All Spectra are now DELETED");

        // create the indexer
        ProjectSpectraIndexer projectSpectraIndexer = new ProjectSpectraIndexer(spectrumIndexBuilder.spectrumSearchService, spectrumIndexBuilder.spectrumIndexService);

        // iterate through project to index spectra
        for (ProjectProvider project : projects) {

            Iterable<? extends ProjectFileProvider> projectFiles = spectrumIndexBuilder.projectFileRepository.findAllByProjectId(project.getId());
            for (ProjectFileProvider projectFile : projectFiles) {
                //TODO: This will change when we have the internal file names in the database

                if (ProjectFileSource.GENERATED.equals(projectFile.getFileSource())) { // TODO: REVIEW THIS FOR SPECTRUM FILE TYPE

                    String fileName = projectFile.getFileName();

                    // TODO
                    // ...

                }
            }
        }
    }


    //TODO: Move it to a pride-archive-utils
    public static String buildAbsoluteMzTabFilePath(String prefix, ProjectProvider project, String fileName) {
        if (project.isPublicProject()) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(project.getPublicationDate());
            int month = calendar.get(Calendar.MONTH) + 1;

            return prefix
                    + File.separator + calendar.get(Calendar.YEAR)
                    + File.separator + (month < 10 ? "0" : "") + month
                    + File.separator + project.getAccession()
                    + File.separator + INTERNAL_FOLDER_NAME
                    + File.separator + translateFromGeneratedToInternalFolderFileName(fileName);
        } else {
            return prefix
                    + File.separator + project.getAccession()
                    + File.separator + INTERNAL_FOLDER_NAME
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
