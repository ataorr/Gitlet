package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;


/** This is the stage class of gitlet.
 * Stage class hold the add files and remove files
 * for the next commit.
 * @author Shantao Ru
 * */
public class Stage implements Serializable {

    /** Directory of the stage file. */
    static final File STAGE_FILE = Utils.join(Gitlet.GITLET_FILE, "stage");

    /** Map that include the file and the filename.
     * HashMap<String filename, File boldFile>
     */
    private HashMap<String, File> addingfiles;

    /** Map that include the removed files. */
    private HashMap<String, File> removefiles;


    /** Initialize the stage file. */
    public static void init() throws IOException {
        if (!STAGE_FILE.exists()) {
            STAGE_FILE.createNewFile();
        }
        Stage stage = new Stage();
        Utils.writeObject(Stage.STAGE_FILE, stage);
    }

    /** Constructor for stage. */
    public Stage() {
        addingfiles = new HashMap<>();
        removefiles = new HashMap<>();
    }

    /** Add track file to stage.
     * @param name name of the file
     * @param file blob file of the target file*/
    public void addFile(String name, File file) {
        addingfiles.put(name, file);
    }

    /** Add remove file to stage.
     * @param name name of the remove file
     * @param file the location of the rm file*/
    public void addrmFile(String name, File file) {
        removefiles.put(name, file);
    }

    /** Return addFiles. */
    public HashMap<String, File> getAddingfiles() {
        return addingfiles;
    }

    /** Return rmFiles. */
    public HashMap<String, File> getRemovefiles() {
        return removefiles;
    }

    /** Clear AddingFiles. */
    public void clearStage() {
        addingfiles.clear();
        removefiles.clear();
    }

    /** Determine if the file have been tracked.
     * @return true of filename is in add stage.
     * @param fileName filename of the search file */
    public boolean contain(String fileName) {
        return addingfiles.containsKey(fileName);
    }

    /** Get bold file.
     * @param name filename of the target file
     * @return blob file of the given filename */
    public File getBolb(String name) {
        return addingfiles.get(name);
    }

    /** Get current blod file with the key.
     * @param key file name of the target file
     * @return blob file base on the given name */
    public File getBoldfile(String key) {
        return addingfiles.get(key);
    }

    /** Determine if stage is empty.
     * @return if the stage is empty. */
    public boolean isEmpty() {
        return (addingfiles.isEmpty() && removefiles.isEmpty());
    }

}
