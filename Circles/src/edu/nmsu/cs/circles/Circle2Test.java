package edu.nmsu.cs.circles;
import org.junit.*;

public class Circle2Test
{
    // Data you need for each test case
    private Circle2 circle;
    @Before
    public void setup()
    {
        circle = new Circle2(1, 1, 1);
    }
    /**!!!TEST CASES!!!**/
    @Test(expected = IllegalArgumentException.class)
    public void negativeRadius(){ //radius is scalar and cannot be <= 0
        Circle2 test = new Circle2(1, 1, -2);
    }
    @Test(expected = IllegalArgumentException.class)
    public void zeroRadius(){ //radius is scalar and cannot be <= 0
        Circle2 test = new Circle2(1, 1, 0);
    }
    @Test //Test a scale by a positive factor
    public void upScale(){
        circle.scale(2);
        Assert.assertTrue(circle.radius == 2);
    }
    @Test //Test a scale by a fractional factor
    public void downScale(){
        circle.scale(0.5);
        Assert.assertTrue(circle.radius == 0.5);
    }
    @Test(expected = IllegalArgumentException.class)
    public void negativeScale(){ //radius is scalar and cannot be <= 0
        circle.scale(-1);
    }
    @Test(expected = IllegalArgumentException.class)
    public void zeroScale(){ //radius is scalar and cannot be <= 0
        circle.scale(0);
    }
    @Test // Test a simple positive move
    public void simpleMove()
    {
        Point p;
        p = circle.moveBy(1, 2);
        Assert.assertTrue(p.x == 2 && p.y == 3);
    }
    @Test // Test a simple negative move
    public void simpleMoveNeg()
    {
        Point p;
        p = circle.moveBy(-1, -2);
        Assert.assertTrue(p.x == 0 && p.y == -1);
    }
    @Test //intersect test: these circles should intersect
    public void intersectTest(){
        Circle2 other = new Circle2(-20, -20, 100);
        boolean test = circle.intersects(other);
        Assert.assertTrue(test);
    }
    @Test //negative intersect test: these circles should not intersect
    public void negIntersectTest(){
        Circle2 other = new Circle2(50, 75, 1);
        boolean test = circle.intersects(other);
        Assert.assertFalse(test);
    }
}