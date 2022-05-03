package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/** This is Blob Class. Which we use it to store the contents of the file.
 * Blob File are all store in the .getlet/Blob directory.
 * @author Shantao Ru
 */
public class Blob implements Serializable {

    /** File directory of this bolbs. */
    static final File BLOBSFOLDER = Utils.join(Gitlet.GITLET_FILE, "Bolbs");

    /** Hashcode of this blob. */
    private String hashcode;

    /** Contents of this blob. */
    private String contents;

    /** File of this blob. */
    private File bolbFile;

    /** Constructor of Bolb class.
     * @param file the file to create blob.
     */
    public Blob(File file) {
        contents = Utils.readContentsAsString(file);
        hashcode = Integer.toString(contents.hashCode());
        bolbFile = Utils.join(BLOBSFOLDER, hashcode);
    }

    /** Init the bold directory. */
    public static void init() {
        if (!BLOBSFOLDER.exists()) {
            BLOBSFOLDER.mkdir();
        }
    }

    /** Write this bold to file. */
    public void writeBold() throws IOException {
        if (!bolbFile.exists()) {
            bolbFile.createNewFile();
        }
        Utils.writeObject(bolbFile, this);
    }

    /** Return blobFiles. */
    public File getBlobFiles() {
        return bolbFile;
    }

    /** Return contents of the blob. */
    public String getContents() {
        return contents;
    }

    /** Overwrite the blob to file.
     * @param target targetFile to overwirte contents.
     * */
    public void overwrite(File target) throws IOException {
        if (!target.exists()) {
            target.createNewFile();
        }
        Utils.writeContents(target, contents);
    }
}
