package gitlet;

import java.io.IOException;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Shantao Ru
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) throws IOException {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        basicCommand(args);
        if (args[0].equals("checkout")) {
            if (args.length == 2) {
                Gitlet.branchCheckout(args[1]);
                System.exit(0);
            } else if (args.length == 3) {
                if (args[1].equals("--")) {
                    Gitlet.basicCheckout(args[2]);
                    System.exit(0);
                }
            } else if (args.length == 4) {
                if (args[2].equals("--")) {
                    Gitlet.commitCheckout(args[1], args[3]);
                    System.exit(0);
                }
            }
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        if (args[0].equals("status")) {
            Gitlet.status();
            System.exit(0);
        }
        if (args[0].equals("rm")) {
            Gitlet.remove(args[1]);
            System.exit(0);
        }
        if (args[0].equals("find")) {
            Gitlet.find(args[1]);
            System.exit(0);
        }
        if (args[0].equals("branch")) {
            Gitlet.branch(args[1]);
            System.exit(0);
        }
        if (args[0].equals("reset")) {
            Gitlet.reset(args[1], true);
            System.exit(0);
        }
        if (args[0].equals("rm-branch")) {
            Gitlet.removeBranch(args[1]);
            System.exit(0);
        }
        if (args[0].equals("merge")) {
            Gitlet.merge(args[1]);
            System.exit(0);
        }
        System.out.println("No command with that name exists.");
    }

    /** This method is use to handle basic gitlet command.
     * @param args the args that were pass from the main method.
     * */
    public static void basicCommand(String[] args) throws IOException {
        if (args[0].equals("init")) {
            Gitlet.init();
            System.exit(0);
        }
        if (args[0].equals("add")) {
            Gitlet.add(args[1]);
            System.exit(0);
        }
        if (args[0].equals("commit")) {
            Gitlet.commit(args[1], null);
            System.exit(0);
        }
        if (args[0].equals("log")) {
            Gitlet.log();
            System.exit(0);
        }
        if (args[0].equals("global-log")) {
            Gitlet.globallog();
            System.exit(0);
        }
    }
}
