
/**
 * Created by delto on 4/14/2016.
 */
public class QuadTree {
    private QTreeNode root;

    /**
     * Created by delto on 4/14/2016.
     */
    public static class QTreeNode {
        private String name;
        private double ullat;
        private double ullon;
        private double lrlat;
        private double lrlon;
        private QTreeNode childNW, childNE, childSW, childSE;

        public QTreeNode(String n, double ulla, double ullo, double lrla, double lrlo) {
            name = n;
            ullat = ulla;
            ullon = ullo;
            lrlat = lrla;
            lrlon = lrlo;
        }

        /** this method traverses the quadtree upwards by concatenating away the final digit
         * of each node's address, then subsequently performing a downward coordinate-based
         * search, traversing upwards multiple levels if needed.
         * @param x
         * @param y
         * @return
         */
        public QTreeNode upwardCoordinateBasedSearch(double x, double y) {
            int address = this.getAddress();

        }

        /** Illusory instance var alert! This method simply uses the name (type: String) instance
         * var to generate an integer "address" according to our quadtree schema.
         * @return the 123 in "123.png"
         */
        public int getAddress() {
            String output = "";
            for (int i = 0; i <= this.name.length() - 5; i++) {
                output = output + Character.toString(this.name.charAt(i));
            }
            return Integer.parseInt(output);
        }

        /** this method allows a tile to "know" if a certain lon, lat coordinate is within its boundaries
         *
         * @param X - longitude
         * @param Y - latitude
         * @return true if the tile contains the lon, lat coorinate.
         */
        public boolean containsXY(double X, double Y) {
            if (X < this.ullon || X > this.lrlon) {
                return false;
            }
            if (Y > this.ullat || Y < this.lrlat) {
                return false;
            } else {
                return true;
            }
        }

        /** this method allows a tile to "know" its own distance per pixel in feet
         *
         * @param depth - depth of the tile
         * @return
         */
        public double generateTileDPP(int depth) {
            return (this.lrlon - this.ullon) * 288200 / (256 ^ depth);
        }

        public double getUllat() {
            return ullat;
        }
        public double getUllon() {
            return ullon;
        }
        public double getLrlat() {
            return lrlat;
        }
        public double getLrlon() {
            return lrlon;
        }
    }

    public static void createCompleteQuadTree(QTreeNode n, int depth) {
        if (depth == 0) {
            return;
        } else {
            n.childNW = new QTreeNode(n.name + "1", n.ullat, n.ullon, (n.ullat + n.lrlat)/2, (n.ullon + n.lrlon)/2);
            n.childNE = new QTreeNode(n.name + "2", n.ullat, (n.ullon + n.lrlon)/2, (n.lrlat + n.ullat)/2, n.lrlon);
            n.childSW = new QTreeNode(n.name + "3", (n.ullat + n.lrlat)/2, n.ullon, n.lrlat, (n.ullon + n.lrlon)/2);
            n.childSE = new QTreeNode(n.name + "4", (n.ullat + n.lrlat)/2, (n.ullon + n.lrlon)/2, n.lrlat, n.lrlon);
            createCompleteQuadTree(n.childNW, depth - 1);
            createCompleteQuadTree(n.childNE, depth - 1);
            createCompleteQuadTree(n.childSW, depth - 1);
            createCompleteQuadTree(n.childSE, depth - 1);
        }
    }



    /** this method is used to find the upper left node/tile that will be included in the raster
     * given the user's screen dimensions and desired dpp
     * @param ullat
     * @param ullon
     * @param lrlat
     * @param lrlon
     * @param dpp
     * @return
     */
    public QTreeNode getNodeWithCorrectDPP(double x, double y, double dpp, QTreeNode n) {
        if (! n.containsXY(x, y)) {
            return null;
        }
        if (n.generateTileDPP(usingFileNameReturnDepth(n)) > dpp) {
            if (n.childNW.containsXY(x, y)) {
                return getNodeWithCorrectDPP(x, y, dpp, n.childNW);
            } else if (n.childNE.containsXY(x, y)) {
                return getNodeWithCorrectDPP(x, y, dpp, n.childNE);
            } else if (n.childSW.containsXY(x, y)) {
                return getNodeWithCorrectDPP(x, y, dpp, n.childSW);
            } else {
                return getNodeWithCorrectDPP(x, y, dpp, n.childSE);
            }
        } else {
            return n;
        }
    }



    /** this is the method that generates the user's requested DPP value when provided screewidth and lon vals
     * @param ullon
     * @param lrlon
     * @param w
     * @return
     */
    public double generateUserWindowDPP(double w, double lrlon, double ullon) {
        return (lrlon - ullon) * 288200 / w;
    }

    /** problem children below */
//    public QTreeNode[][] findAndOrderRasterImages(double ullon, double lrlon, double h, double w, double ullat, double lrlat) {
//        double dpp = (lrlon - ullon) * 288200 / w;
//        QTreeNode upperLeft = getNodeWithCorrectDPP(ullat, ullon, dpp, w, root);
//        int depthOfImages = usingFileNameReturnDepth(upperLeft);
//
//    }

    public QTreeNode findEasternNeighborOfSameDepth(QTreeNode n) {
        int depth = usingFileNameReturnDepth(n);
        int address = usingFileNameReturnAddress(n);
        if (address % 2 == 1) {
            return lookupQTreeNodeByAddress(root, usingFileNameReturnAddress(n) + 1);
        } else {
            getNodeWithCorrectDPP(n.getLrlon() + (n.getLrlon() - n.getUllon()) / 2,
                    n.getLrlat() + (n.getUllat() - n.getLrlat()) / 2,
                    n.generateTileDPP(usingFileNameReturnDepth(n)), root);
        }
    }

    /** currently only works by passing the root as q
     *
     * @param q - initially pass the root, then uses recursion
     * @param n - address of desired node
     * @return
     */
    public QTreeNode lookupQTreeNodeByAddress(QTreeNode q, int n) {
        if (n < 10) {
            if (n == 1) {
                return q.childNW;
            } else if (n == 2) {
                return q.childNE;
            } else if (n == 3) {
                return q.childSW;
            } else {
                return q.childSE;
            }
        } else {
            int firstDigit = Integer.parseInt(Integer.toString(n).substring(0, 1));
            int remainingDigits = Integer.parseInt(Integer.toString(n).substring(1, String.valueOf(n).length() - 1));
            if (firstDigit == 1) {
                return lookupQTreeNodeByAddress(q.childNW, remainingDigits);
            } else if (firstDigit == 2) {
                return lookupQTreeNodeByAddress(q.childNE, remainingDigits);
            } else if (firstDigit == 3) {
                return lookupQTreeNodeByAddress(q.childSW, remainingDigits);
            } else {
                return lookupQTreeNodeByAddress(q.childSE, remainingDigits);
            }
        }
    }

    public int usingFileNameReturnAddress(QTreeNode n) {
        String Stringpng = n.name;
        String output = "";
        for (int i = 0; i <= Stringpng.length() - 5; i++) {
            output = output + Character.toString(Stringpng.charAt(i));
        }
        return Integer.parseInt(output);
    }

    public int usingFileNameReturnDepth(QTreeNode n) {
        if (n.name == "root.png") {
            return 0;
        } else {
            return n.name.length() - 4;
        }
    }

    public static void main(String[] args) {
            /* Longitude == x-axis; latitude == y-axis.*/
        double ROOT_ULLAT = 37.892195547244356, ROOT_ULLON = -122.2998046875,
                ROOT_LRLAT = 37.82280243352756, ROOT_LRLON = -122.2119140625;
        String ROOT_NAME = "root.png";
        QTreeNode root = new QTreeNode(ROOT_NAME, ROOT_ULLAT, ROOT_ULLON, ROOT_LRLAT, ROOT_LRLON);
        createCompleteQuadTree(root, 2);
    }

}
