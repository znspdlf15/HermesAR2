package hermes.mju.captdesign.hermesar;

import java.util.ArrayList;

/**
 * Created by mju4 on 2018-05-01.
 */

public class LineNodeList {
    static private ArrayList node;


    private LineNodeList(){

    }

    private static class holder{
        public static final LineNodeList INSTANCE = new LineNodeList();
    }

    public static LineNodeList getInstance(){
        return holder.INSTANCE;
    }
    public static ArrayList getNode(){
        return node;
    }

    public static void addToNode(Object o){
        node.add(o);
    }

    public static int getLength(){
        return node.size();
    }

    public static String getCoordinates(int n){
        return node.get(n).toString();
    }
}
