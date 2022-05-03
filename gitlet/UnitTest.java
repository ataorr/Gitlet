package gitlet;

import ucb.junit.textui;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/** The suite of all JUnit tests for the gitlet package.
 *  @author
 */
public class UnitTest {

    /**
     * Run the JUnit tests in the loa package. Add xxxTest.class entries to
     * the arguments of runClasses to run other JUnit tests.
     */
    public static void main(String[] ignored) {
        System.exit(textui.runClasses(UnitTest.class));
    }

    /**
     * A dummy test to avoid complaint.
     */
    @Test
    public void placeholderTest() {
        System.out.println("abc".contains("ab"));
    }

    @Test
    public void initTest() throws IOException {
        Gitlet.init();
        assertTrue(Gitlet.GITLET_FILE.exists());
        assertTrue(Branch.BRANCH_FILE.exists());
        assertTrue(Stage.STAGE_FILE.exists());
    }

    @Test
    public void addTest() throws IOException {
        Gitlet.add("Hello.txt");
        Gitlet.commit("commit", null);
        Gitlet.branch("other");
    }

    @Test
    public void checkoutTest() throws IOException {
        Gitlet.basicCheckout("Hello.txt");
    }

    @Test
    public void logTest() {
        Gitlet.globallog();
    }

}




