package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;


/** This is the Commit class of the gitlet.
 * Commits were store under the directory
 * .getlet/Commit, filename will be their commidID
 * @author Shantao Ru
 */
public class Commit implements Serializable {

    /** directory of commit file. */
    static final File COMMIT_FILE = Utils.join(Gitlet.GITLET_FILE, "commit");

    /** Timestamp for this commit. */
    private String timestamp;

    /** Message for this commit. */
    private String message = new String();

    /** Hashmap that hold the commit files bolb hashcode.
     * HashMap<FileName, BoldFile>
     */
    private HashMap<String, File> files = new HashMap<>();

    /** parent of this commit. */
    private File parent = null;

    /** parent 2 of this commit. */
    private File parent2 = null;

    /** Constructor of commit class.
     * @param gievnMessage commit message
     * @param gievnParent commit parent
     * @param giveTime  timestamp of commit
     * */
    public Commit(String gievnMessage, String giveTime, File gievnParent) {
        message = gievnMessage;
        parent = gievnParent;
        timestamp = giveTime;
    }

    /** Init commit file. */
    public static void init() throws IOException {
        if (!COMMIT_FILE.exists()) {
            COMMIT_FILE.mkdir();
        }
        String initTime = "Thu Jan 1 00:00:00 1970 +0000";
        Commit initCommit = new Commit("initial commit", initTime, null);
        String sha1Code = Utils.sha1(Utils.serialize(initCommit));
        File initCommitFile = Utils.join(COMMIT_FILE, sha1Code);
        Utils.writeObject(initCommitFile, initCommit);
        Branch master = new Branch("master", initCommitFile);
        Utils.writeObject(Branch.MASTER_FILE, master);
        Head head = new Head(Branch.MASTER_FILE, initCommitFile);
        head.save();
    }

    /** Return the tracking file of this commit. */
    public HashMap<String, File> getFiles() {
        return files;
    }

    /** Add tracking files to the commit.
     * @param newfiles files form the stage to add.
     * */
    public void addFiles(HashMap<String, File> newfiles) {
        files = newfiles;
    }

    /** Return timestamp in string format. */
    public static String getTimestamp() {
        SimpleDateFormat time = new SimpleDateFormat();
        time.applyPattern("EEE MMM d HH:mm:ss yyyy");
        Date date = new Date();
        return time.format(date) + " -0800";
    }

    /** Return sha1 commitID. */
    public String commitID() {
        return Utils.sha1(Utils.serialize(this));
    }

    /** Return commit message of this commit. */
    public String getMessage() {
        return message;
    }

    /** Return log information of this commit. */
    public String log() {
        String output = "===\n";
        output += "commit " + commitID() + "\n";
        output += "Date: " + timestamp + "\n";
        output += message + "\n";
        return output;
    }

    /** Return parent of this commit. */
    public File getParent() {
        return parent;
    }

    /** Return parent2 of this commit. */
    public File getParent2() {
        return parent2;
    }

    /** Change parent2 of this commit.
     * @param file add parent2 file to commit.
     * */
    public void changeParent2(File file) {
        parent2 = file;
    }

    /** Return commitFile of this file. */
    public File getcommitFile() {
        return Utils.join(COMMIT_FILE, commitID());
    }

    /** Return require blob files stored in files.
     * @param filename filename of the given blob
     * */
    public File getBlob(String filename) {
        if (!files.containsKey(filename)) {
            System.out.println("File does not exist in that commit.");
            System.out.println(0);
        }
        return files.get(filename);
    }
}
