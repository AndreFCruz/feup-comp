package worker.workers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import report.WorkerReport;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.support.reflect.code.CtBlockImpl;

import static org.junit.jupiter.api.Assertions.*;

class W_classMethodsPrivateTest {
    private static CtClass c = Launcher.parseClass("abstract class A { void m() { System.out.println(\"yeah\");} private String s(){return \"\";} private int ii(){ return 10;} protected double pp(){ return 0.0;} abstract fff();");

    private static W_classMethodsPrivate w = new W_classMethodsPrivate(c, "");

    @BeforeEach
    void setUp() {
        w.filter = w.setFilter();
    }

    @Test
    void setFilter() {
        assertTrue(w.filter.matches(c));
        assertFalse(w.filter.matches(new CtBlockImpl()));
    }

    @Test
    void call() throws Exception {
        assertEquals((new WorkerReport(2)).getValue(), w.call().getValue());
    }

}