package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/** This is the main class of the gitlet.
 * This class take args from the main method and
 * operate correspond command base on the args.
 * @author Shantao Ru */
public class Gitlet {

    /** This is the current working folder. */
    static final File CWD_FOLD = new File(System.getProperty("user.dir"));

    /** This is the gitlet folder under CWD. */
    static final File GITLET_FILE = Utils.join(CWD_FOLD, ".gitlet");

    /** The init method of gitlet, create a repo in CWD. */
    public static void init() throws IOException {
        if (GITLET_FILE.exists()) {
            System.out.println("A Gitlet version-control system already "
                   + "exists in the current directory.");
            System.exit(0);
        }
        GITLET_FILE.mkdir();
        Head.init();
        Branch.init();
        Stage.init();
        Commit.init();
        Blob.init();
        Stage.init();
    }

    /** The add command for gitlet.
     * @param filename name of file to be add */
    public static void add(String filename) throws IOException {
        checkInit();
        File newFile = Utils.join(CWD_FOLD, filename);
        if (!newFile.exists()) {
            System.out.println("File does not exists");
            System.exit(0);
        }
        Blob newBolb = new Blob(newFile);
        File blobFile = newBolb.getBlobFiles();
        Stage stage = Utils.readObject(Stage.STAGE_FILE, Stage.class);
        if (!stage.contain(filename)) {
            newBolb.writeBold();
        }
        if (stage.getRemovefiles().containsKey(filename)) {
            stage.getRemovefiles().remove(filename);
        }
        stage.addFile(filename, blobFile);

        Head head = Utils.readObject(Head.HEAD_FILE, Head.class);
        Commit lastCommit = head.getCurrentCommit();
        if (lastCommit.getFiles().containsKey(filename)) {
            File lastBlobFile = lastCommit.getBlob(filename);
            Blob lastBlob = Utils.readObject(lastBlobFile, Blob.class);
            if (lastBlob.getContents().equals(newBolb.getContents())) {
                stage.getAddingfiles().remove(filename);
            }
        }
        Utils.writeObject(Stage.STAGE_FILE, stage);
    }

    /** This is the commit method.
     * @param message the commit message
     * @param parent prarent2 of the commit */
    public static void commit(String message, File parent) throws IOException {
        checkInit();
        if (message.equals("")) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }

        Head head = Utils.readObject(Head.HEAD_FILE, Head.class);
        Branch curBranch = head.getCurrentBranch();

        Stage stage = Utils.readObject(Stage.STAGE_FILE, Stage.class);
        File headPointer = curBranch.getBranchpointer();
        Commit lastCommit = Utils.readObject(headPointer, Commit.class);
        if (stage.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        HashMap<String, File> files = lastCommit.getFiles();
        for (String f : stage.getAddingfiles().keySet()) {
            File addFile = Utils.join(CWD_FOLD, f);
            File blobFile = stage.getBoldfile(f);
            if (!addFile.exists()) {
                Blob b = Utils.readObject(blobFile, Blob.class);
                b.overwrite(addFile);
            }
            files.put(f, stage.getBoldfile(f));
        }
        for (String f : stage.getRemovefiles().keySet()) {
            File rmFile = Utils.join(CWD_FOLD, f);
            Utils.restrictedDelete(rmFile);
            files.remove(f);
        }
        Commit newCommit = new Commit(message,
                Commit.getTimestamp(), headPointer);
        if (parent != null) {
            newCommit.changeParent2(parent);
        }
        newCommit.addFiles(files);
        String newCommitID = newCommit.commitID();
        File newCommitFile = Utils.join(Commit.COMMIT_FILE, newCommitID);
        head.changeCommit(newCommitFile);
        curBranch.addCommit(newCommitFile);
        stage.clearStage();
        Utils.writeObject(newCommitFile, newCommit);
        Utils.writeObject(Stage.STAGE_FILE, stage);
        curBranch.save();
        head.save();
    }

    /** This is the log command
     * it read head form the head file.
     * iterate all parents from that commit. */
    public static void log() {
        checkInit();
        Head head = Utils.readObject(Head.HEAD_FILE, Head.class);
        Branch currentBranch = head.getCurrentBranch();
        File headPointer = currentBranch.getBranchpointer();
        while (headPointer != null) {
            Commit current = Utils.readObject(headPointer, Commit.class);
            System.out.println(current.log());
            headPointer = current.getParent();
        }

    }

    /** This is the normal checkout method.
     * @param filename file to be restore */
    public static void basicCheckout(String filename) throws IOException {
        checkInit();
        Head head = Utils.readObject(Head.HEAD_FILE, Head.class);
        Commit headCommit = head.getCurrentCommit();
        File targetFile = Utils.join(CWD_FOLD, filename);
        if (!targetFile.exists()) {
            targetFile.createNewFile();
        }
        File blobFile = headCommit.getBlob(filename);
        Blob lastBlob = Utils.readObject(blobFile, Blob.class);
        Utils.writeContents(targetFile, lastBlob.getContents());
    }

    /** Commit chekcout method.
     * @param commitID uid of the commit.
     * @param filename filename of the restore file. */
    public static void commitCheckout(String commitID, String filename) {
        checkInit();
        String fullID = commitID;
        if (commitID.length() == 8) {
            fullID = getFullid(commitID);
        }
        File targetFile = Utils.join(CWD_FOLD, filename);
        File commitFile = Utils.join(Commit.COMMIT_FILE, fullID);
        if (!commitFile.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Commit commit = Utils.readObject(commitFile, Commit.class);
        if (!commit.getFiles().containsKey(filename)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        File blobFile = commit.getBlob(filename);
        Blob blob = Utils.readObject(blobFile, Blob.class);
        Utils.writeContents(targetFile, blob.getContents());
    }

    /** A helper method that find out full uid base on short uid.
     * @param commitID short-uid
     * @return full-uid */
    public static String getFullid(String commitID) {
        List<String> allCommits = Utils.plainFilenamesIn(Commit.COMMIT_FILE);
        for (int i = 0; i < allCommits.size(); i++) {
            String name = allCommits.get(i);
            if (name.contains(commitID)) {
                return name;
            }
        }
        return new String();
    }

    /** Branch checkout method, reset the cwd to given branch pointer.
     * @param branchname name of the target branch. */
    public static void branchCheckout(String branchname) throws IOException {
        checkInit();
        File targetBranchFile = Utils.join(Branch.BRANCH_FILE, branchname);
        if (!targetBranchFile.exists()) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        Head head = Utils.readObject(Head.HEAD_FILE, Head.class);
        Branch targetBranch = Utils.readObject(targetBranchFile, Branch.class);
        Branch curBranch = head.getCurrentBranch();
        if (targetBranch.getName().equals(curBranch.getName())) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        File branchCommitFile = targetBranch.getBranchpointer();
        Commit branchCommit = Utils.readObject(branchCommitFile, Commit.class);
        String commitID = branchCommit.commitID();
        reset(commitID, false);
        head.changeBranch(targetBranchFile);
        head.changeCommit(branchCommitFile);
        head.save();
    }

    /** This method scan the cwd and return files that were untracked.
     * @param commit head commit of gitlet. */
    public static ArrayList<String> untrackFiles(Commit commit) {
        HashMap<String, File> commitFiles = commit.getFiles();
        List<String> cwdFiles = Utils.plainFilenamesIn(CWD_FOLD);
        ArrayList<String> untrackFiles = new ArrayList<>();
        Stage stage = Utils.readObject(Stage.STAGE_FILE, Stage.class);
        for (int i = 0; i < cwdFiles.size(); i++) {
            String filename = cwdFiles.get(i);
            if (commitFiles.containsKey(filename)) {
                continue;
            } else if (stage.contain(filename)) {
                continue;
            } else if (filename.equals("g.txt")) {
                continue;
            } else if (ignore(filename)) {
                continue;
            } else {
                untrackFiles.add(filename);
            }
        }
        return untrackFiles;
    }

    /** Reset method that set the cwd as the given commit.
     * @param commitID uid of the commit
     * @param isReset if it is call from the main method. */
    public static void reset(String commitID,
                             boolean isReset) throws IOException {
        File commitFile = Utils.join(Commit.COMMIT_FILE, commitID);
        if (!commitFile.exists()) {
            System.out.println("No commit with that id exists");
            System.exit(0);
        }
        Head head = Utils.readObject(Head.HEAD_FILE, Head.class);
        Commit commit = Utils.readObject(commitFile, Commit.class);
        HashMap<String, File> commitFiles = commit.getFiles();
        ArrayList<String> untrackFiles = untrackFiles(head.getCurrentCommit());
        if (!untrackFiles.isEmpty()) {
            System.out.println("There is an untracked file in the way; "
                  +  "delete it, or add and commit it first.");
            System.exit(0);
        }
        List<String> files = Utils.plainFilenamesIn(CWD_FOLD);
        for (int i = 0; i < files.size(); i++) {
            String filename = files.get(i);
            if (!commitFiles.containsKey(filename) && !ignore(filename)) {
                Utils.restrictedDelete(filename);
            }
        }
        for (String s : commitFiles.keySet()) {
            Blob blob = Utils.readObject(commitFiles.get(s),
                    Blob.class);
            blob.overwrite(Utils.join(CWD_FOLD, s));
        }
        Stage stage = Utils.readObject(Stage.STAGE_FILE, Stage.class);
        stage.clearStage();
        Utils.writeObject(Stage.STAGE_FILE, stage);
        if (isReset) {
            Branch branch = head.getCurrentBranch();
            branch.changeBranchpointer(commitFile);
            branch.save();
        }
    }

    /** branch remove method that will remove the given branch.
     * @param branchName targte branch name to remove */
    public static void removeBranch(String branchName) {
        File branchFile = Utils.join(Branch.BRANCH_FILE, branchName);
        if (!branchFile.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        Head head = Utils.readObject(Head.HEAD_FILE, Head.class);
        Branch curBranch = head.getCurrentBranch();
        Branch rmBranch = Utils.readObject(branchFile, Branch.class);
        if (rmBranch.getName().equals(curBranch.getName())) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        branchFile.delete();
    }

    /** Determine if the filename should be ignore in untrackfiles.
     * @param filename name of the file
     * @return true if it should ignore, false otherwise */
    public static boolean ignore(String filename) {
        if (filename.equals(".DS_Store")) {
            return true;
        } else if (filename.equals(".gitignore")) {
            return true;
        } else if (filename.equals("Makefile")) {
            return true;
        } else if (filename.equals("proj3.iml")) {
            return true;
        }
        return false;
    }

    /** Display the status of the gitlet stage. */
    public static void status() {
        checkInit();
        Stage stage = Utils.readObject(Stage.STAGE_FILE, Stage.class);
        Head head = Utils.readObject(Head.HEAD_FILE, Head.class);
        String curBranchName = head.getCurrentBranch().getName();
        List<String> branches = Utils.plainFilenamesIn(Branch.BRANCH_FILE);
        System.out.println("=== Branches ===");
        for (int i = 0; i < branches.size(); i++) {
            if (curBranchName.equals(branches.get(i))) {
                System.out.println("*" + curBranchName);
            } else {
                System.out.println(branches.get(i));
            }
        }

        System.out.println("\n=== Staged Files ===");
        for (String a : stage.getAddingfiles().keySet()) {
            System.out.println(a);
        }
        System.out.println("\n=== Removed Files ===");
        for (String r : stage.getRemovefiles().keySet()) {
            System.out.println(r);
        }

        System.out.println("\n=== Modifications Not Staged For Commit ===");
        System.out.println("\n=== Untracked Files ===\n");

    }

    /** Display of all commits in the history.
     * Iterate all file in .getlet/Commit */
    public static void globallog() {
        List<String> commits = Utils.plainFilenamesIn(Commit.COMMIT_FILE);
        for (int i = 0; i < commits.size(); i++) {
            File f = Utils.join(Commit.COMMIT_FILE, commits.get(i));
            Commit c = Utils.readObject(f, Commit.class);
            System.out.println(c.log());
        }
    }

    /** Create new branch in the current gitlet.
     * @param branchName new branch name */
    public static void branch(String branchName) {
        Head head = Utils.readObject(Head.HEAD_FILE, Head.class);
        Branch curBranch = head.getCurrentBranch();
        File newBranchFile = Utils.join(Branch.BRANCH_FILE, branchName);
        if (newBranchFile.exists()) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        Branch newBranch = new Branch(branchName, curBranch.getBranchpointer());
        newBranch.save();
    }

    /** remove the given branch if it exists.
     * @param filename name of the remove branch */
    public static void remove(String filename) {
        File rmFile = Utils.join(CWD_FOLD, filename);
        Stage stage = Utils.readObject(Stage.STAGE_FILE, Stage.class);
        Head head = Utils.readObject(Head.HEAD_FILE, Head.class);
        Commit lastCommit = head.getCurrentCommit();
        HashMap<String, File> stageFiles = stage.getAddingfiles();
        HashMap<String, File> commitFiles = lastCommit.getFiles();

        if (stageFiles.containsKey(filename)) {
            stageFiles.remove(filename);
        } else if (commitFiles.containsKey(filename)) {
            stage.addrmFile(filename, rmFile);
            if (rmFile.exists()) {
                Utils.restrictedDelete(rmFile);
            }
        } else {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        Utils.writeObject(Stage.STAGE_FILE, stage);
    }

    /** Find commit if it has the given message.
     * @param message commit message to find */
    public static void find(String message) {
        List<String> commits = Utils.plainFilenamesIn(Commit.COMMIT_FILE);
        boolean isEmypty = true;
        for (int i = 0; i < commits.size(); i++) {
            File f = Utils.join(Commit.COMMIT_FILE, commits.get(i));
            Commit c = Utils.readObject(f, Commit.class);
            if (c.getMessage().equals(message)) {
                System.out.println(c.commitID());
                isEmypty = false;
            }
        }
        if (isEmypty) {
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }
    }

    /** Merge the given branch into current branch.
     * @param branchName name of the branch to merge */
    public static void merge(String branchName) throws IOException {
        File targetBranchFile = Utils.join(Branch.BRANCH_FILE, branchName);
        mergeCheck(targetBranchFile);
        Branch targetBranch = Utils.readObject(targetBranchFile, Branch.class);
        Head head = Utils.readObject(Head.HEAD_FILE, Head.class);
        Commit curHead = head.getCurrentCommit();
        Commit bracHead = targetBranch.getHeadCommit();
        Commit splitPoint = findSplitpoint(curHead, bracHead);
        mergeSpecial(curHead, bracHead, splitPoint);
        mergeFiles(splitPoint, curHead, bracHead);
        String curBranchName = head.getCurrentBranch().getName();
        String message = "Merged " + branchName
                + " into " + curBranchName + ".";
        commit(message, bracHead.getcommitFile());
    }

    /** check if there are any false cases appeared.
     * @param targetBranchFile name of the merge branch */
    public static void mergeCheck(File targetBranchFile) {
        checkInit();
        if (!targetBranchFile.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        Head head = Utils.readObject(Head.HEAD_FILE, Head.class);
        Stage stage = Utils.readObject(Stage.STAGE_FILE, Stage.class);
        Branch targetBranch = Utils.readObject(targetBranchFile, Branch.class);
        if (head.getCurrentBranch().getName().equals(targetBranch.getName())) {
            System.out.println("Cannot merge a branch with itself");
            System.exit(0);
        }
        if (!stage.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        Commit curCommit = head.getCurrentCommit();
        ArrayList<String> untrackFiles = untrackFiles(curCommit);
        if (!untrackFiles.isEmpty()) {
            System.out.println("There is an untracked file in the way;"
                    + " delete it," + " or add and commit it first.");
            System.exit(0);
        }
    }

    /** Find the splitpoint of the two given commit.
     * @param commit1 first commit
     * @param commit2 second commit
     * @return splitpoint commit, null if no splitpoint */
    public static Commit findSplitpoint(Commit commit1, Commit commit2) {
        Commit pointer1 = commit1;
        Commit pointer2 = commit2;
        while (pointer1.getParent() != null) {
            pointer2 = commit2;
            if (pointer1.getParent2() != null) {
                pointer1 = Utils.readObject(
                        pointer1.getParent2(), Commit.class);
                return pointer1;
            }
            if (pointer1.commitID().equals(commit2.commitID())) {
                return pointer1;
            }
            pointer1 = Utils.readObject(pointer1.getParent(), Commit.class);
            while (pointer2.getParent() != null) {
                pointer2 = Utils.readObject(pointer2.getParent(), Commit.class);
                String id1 = pointer1.commitID();
                String id2 = pointer2.commitID();
                if (id1.equals(id2)) {
                    return pointer1;
                }
                if (id2.equals(commit1.commitID())) {
                    return pointer2;
                }
            }
        }
        throw new GitletException("Can not find splitpoint");
    }

    /** Special case that might encounter in merging branch.
     * @param curCommit current commit
     * @param brnCommit branch commit
     * @param splitCommit split point commit */
    public static void mergeSpecial(Commit curCommit,
                                     Commit brnCommit,
                                     Commit splitCommit) throws IOException {

        if (splitCommit.commitID().equals(curCommit.commitID())) {
            reset(brnCommit.commitID(), true);
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        } else if (splitCommit.commitID().equals(brnCommit.commitID())) {
            System.out.println("Given branch is an "
                    + "ancestor of the current branch.");
            System.exit(0);
        }
    }

    /** Handle files during merge and clear the stage afterward.
     * @param current current commit
     * @param other branch commit
     * @param split split point commit */
    public static void mergeFiles(
            Commit split, Commit current, Commit other) throws IOException {
        HashMap<String, File> splitFiles = split.getFiles();
        HashMap<String, File> currentFiles = current.getFiles();
        HashMap<String, File> otherFiles = other.getFiles();
        HashMap<String, File> allFiles = new HashMap<>();
        allFiles.putAll(splitFiles);
        allFiles.putAll(currentFiles);
        allFiles.putAll(otherFiles);
        Stage stage = Utils.readObject(Stage.STAGE_FILE, Stage.class);
        for (String s : allFiles.keySet()) {
            int num = caseIdentify(splitFiles, currentFiles, otherFiles, s);
            File targetFile = Utils.join(CWD_FOLD, s);
            File blobFile;
            switch (num) {
            case 3:
            case 7:
                break;
            case 0:
                mergeConflict(currentFiles, otherFiles, s);
                break;
            case 1:
            case 5:
                blobFile = otherFiles.get(s);
                stage.addFile(s, blobFile);
                break;
            case 2:
            case 4:
                blobFile = currentFiles.get(s);
                stage.addFile(s, blobFile);
                break;
            case 6:
                stage.addrmFile(s, Utils.join(CWD_FOLD, s));
                break;
            default:
                break;
            }
        }
        Utils.writeObject(Stage.STAGE_FILE, stage);
    }

     /** Handle mergeConflict if it happend
      * crate and write the conflict file into the cwd and
      * send message to the terminal.
      * @param curFiles current commit files
      * @param othFiles branch commit files
      * @param filename name of the conflict file */
    public static void mergeConflict(HashMap<String, File> curFiles,
                                       HashMap<String, File> othFiles,
                                       String filename) throws IOException {

        String newContents = "<<<<<<< HEAD\n";
        if (curFiles.containsKey(filename)) {
            Blob curBlob = Utils.readObject(curFiles.get(filename), Blob.class);
            newContents += curBlob.getContents();
        }
        newContents += "=======\n";
        if (othFiles.containsKey(filename)) {
            Blob othBlob = Utils.readObject(othFiles.get(filename), Blob.class);
            newContents += othBlob.getContents();
        }
        newContents += ">>>>>>>\n";
        File targetFile = Utils.join(CWD_FOLD, filename);
        Utils.writeContents(targetFile, newContents);
        add(filename);
        System.out.println("Encountered a merge conflict.");
    }

     /** Determine which case fit for each files in the cwd directory.
      * @param splitFiles all track files in the split point commit
      * @param currentFiles all track files in the current commit
      * @param otherFiles all track files in the branch commit
      * @param filename name of the file to determine
      * @return a number that represent the case */
    public static int caseIdentify(HashMap<String, File> splitFiles,
                                    HashMap<String, File> currentFiles,
                                    HashMap<String, File> otherFiles,
                                    String filename) {
        if (currentFiles.containsKey(filename)
                && otherFiles.containsKey(filename)) {
            if (!splitFiles.containsKey(filename)) {
                return 0;
            }
            if (containSame(currentFiles, splitFiles, filename)
                    && containSame(otherFiles, splitFiles, filename)) {
                return 3;
            } else if (containSame(currentFiles, splitFiles, filename)) {
                return 1;
            } else if (containSame(otherFiles, splitFiles, filename)) {
                return 2;
            } else {
                return 0;
            }
        } else if (splitFiles.containsKey(filename)
                && currentFiles.containsKey(filename)) {
            if (containSame(currentFiles, splitFiles, filename)) {
                return 6;
            }
            return 0;
        } else if (splitFiles.containsKey(filename)
                && otherFiles.containsKey(filename)) {
            if (containSame(otherFiles, splitFiles, filename)) {
                return 7;
            }
            return 0;
        } else if (currentFiles.containsKey(filename)) {
            return 4;
        } else if (otherFiles.containsKey(filename)) {
            return 5;
        }
        return 6;
    }

    /** Determine if two track commits file have the same blob file.
     * @param map1 commit files 1
     * @param map2 commit files 2
     * @param fileName name of the file to determine
     * @return return true contain same, false otherwise. */
    public static boolean containSame(HashMap<String, File> map1,
                                       HashMap<String, File> map2,
                                       String fileName) {
        File file1 = map1.get(fileName);
        File file2 = map2.get(fileName);
        return file1.equals(file2);
    }

    /** Check if the gitlet directory exists. */
    public static void checkInit() {
        if (!GITLET_FILE.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }


}
