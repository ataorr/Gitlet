package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedList;

/** This is the Branch class of the Gitlet.
 * This class will store in the branch directory.
 * .getlet/Branch
 * @author Shantao Ru
 * */
public class Branch implements Serializable {

    /** The file of the branch directory. */
    static final File BRANCH_FILE = Utils.join(Gitlet.GITLET_FILE, "Branches");

    /** Master branch file. */
    static final File MASTER_FILE = Utils.join(BRANCH_FILE, "master");

    /** All commits of this branch (String CommitID). */
    private LinkedList<File> commits;

    /** Head Pointer of this branch. */
    private File branchpointer;

    /** Name of this branch. */
    private String name;

    /** File of this branch. */
    private File branchFile;

    /** Constructor for Branch class.
     * @param nameBranch name of the given branch
     * @param branchPointer name of the give pointer. */
    public Branch(String nameBranch, File branchPointer) {
        branchFile = Utils.join(BRANCH_FILE, nameBranch);
        name = nameBranch;
        branchpointer = branchPointer;
        commits = new LinkedList<>();
    }

    /** Init for branch file directory. */
    public static void init() {
        if (!BRANCH_FILE.exists()) {
            BRANCH_FILE.mkdir();
        }
    }

    /** Return branch pointer. */
    public File getBranchpointer() {
        return branchpointer;
    }

    /** Change branch pointer.
     * @param newPointer file of the given new pointer.*/
    public void changeBranchpointer(File newPointer) {
        branchpointer = newPointer;
    }

    /** Return the name of this branch. */
    public String getName() {
        return name;
    }

    /** Return head Commit. */
    public Commit getHeadCommit() {
        if (branchpointer.exists()) {
            Commit commit = Utils.readObject(branchpointer, Commit.class);
            return commit;
        } else {
            throw new GitletException("Head Commit does not exists.");
        }
    }


    /** Add commit to the current branch.
     * @param commitFile file of the commit to add. */
    public void addCommit(File commitFile) {
        branchpointer = commitFile;
        commits.add(commitFile);
    }

    /** Save this branch. */
    public void save() {
        Utils.writeObject(branchFile, this);
    }
}
