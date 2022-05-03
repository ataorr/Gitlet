package gitlet;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;


/** This is the head class of gitlet.
 * This class store the head commit and head branch
 * in to the file .getlet/head
 * @author Shantao Ru
 */
public class Head implements Serializable {

    /** headFile file. */
    static final File HEAD_FILE = Utils.join(Gitlet.GITLET_FILE, "head");

    /** currentHeadBranch File. */
    private File currentBranch;

    /** currentHeadCommit File. */
    private File currentCommit;

    /** Constructor of this Head Class.
     * @param branch head branch
     * @param commit head commit */
    Head(File branch, File commit) {
        currentBranch = branch;
        currentCommit = commit;
    }

    /** Init method for head. */
    static void init() throws IOException {
        if (!HEAD_FILE.exists()) {
            HEAD_FILE.createNewFile();
        }
    }

    /** Return current Branch File. */
    public Branch getCurrentBranch() {
        if (currentBranch.exists()) {
            Branch b = Utils.readObject(currentBranch, Branch.class);
            return b;
        } else {
            throw new GitletException("Branch does not exists.");
        }
    }

    /** Return current Commit File. */
    public Commit getCurrentCommit() {
        if (currentCommit.exists()) {
            Commit c = Utils.readObject(currentCommit, Commit.class);
            return c;
        } else {
            throw new GitletException("Commit does not exists.");
        }
    }

    /** Change current Branch File.
     * @param newBranch new branch of the gitlet
     * */
    public void changeBranch(File newBranch) {
        currentBranch = newBranch;
    }

    /** Change current Commit File.
     * @param newCommit new commmit head of gitlet */
    public void changeCommit(File newCommit) {
        currentCommit = newCommit;
    }

    /** Save itself to head file. */
    public void save() {
        Utils.writeObject(HEAD_FILE, this);
    }
}
