import java.io.PrintStream;
import java.util.PriorityQueue;
import java.util.Scanner;

/**
 * Name: Wesley Pang 
 * Date: 12/8/16
 *
 * This class compresses data. This class can also decompress the compressed
 * data back into its original form. The class also saves the translation code
 * between the original and compressed data.
 *
 */
public class HuffmanCode {
    private HuffmanNode root;

    /**
     * Huffman nodes are data structures that hold data containing the character
     * and its respective frequency. The characters are stored in their ASCII
     * numerical format. The nodes also hold references to the next nodes.
     * HuffmanNodes are comparable.
     *
     */
    private static class HuffmanNode implements Comparable<HuffmanNode> {
        // character data in ASCII
        public final int data;
        // number of occurrences of character
        public final int freq;
        public HuffmanNode zero;
        public HuffmanNode one;
        // input value for data and freq when their values do not matter
        private static final int PLACE_HOLDER = -1;

        /**
         * default constructor
         */
        public HuffmanNode() {
            this(PLACE_HOLDER, PLACE_HOLDER, null, null);
        }

        /**
         * constructor for basic nodes
         *
         * @param data
         *            character data in ASCII
         * @param freq
         *            number of occurrences of characters
         */
        public HuffmanNode(int data, int freq) {
            this(data, freq, null, null);
        }

        /**
         * general constructor
         *
         * @param data
         *            character data in ASCII
         * @param freq
         *            number of occurrences of characters
         * @param zero
         *            pointer to left node zero path
         * @param one
         *            pointer to right node one path
         */
        public HuffmanNode(int data, int freq, HuffmanNode zero, HuffmanNode one) {
            this.data = data;
            this.freq = freq;
            this.zero = zero;
            this.one = one;
        }

        /**
         * compare to method that makes Huffman Nodes comparable. Compares nodes
         * by comparing their frequencies. Lower frequencies are ranked lower.
         * Ties are decided arbitrarily(by the computer).
         * 
         * @param other
         *            the other huffman node to compare to
         */
        @Override
        public int compareTo(HuffmanNode other) {
            return Integer.compare(this.freq, other.freq);
        }
    }

    /**
     * Constructor that takes an integer array of the counts of each character.
     * The array should have each character's count in its respective ASCII
     * index. Constructs the object so characters with the highest count have
     * the shortest code. Assumes that there are at least two different
     * characters with non-zero counts
     *
     * @param count
     *            the array of counts for each respective ASCII character
     */
    public HuffmanCode(int[] count) {
        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();
        // adds huffman nodes with character and count to the priority queue. If
        // the character's count is zero, it is ignored.
        for (int i = 0; i < count.length; i++) {
            if (count[i] != 0) {
                pq.add(new HuffmanNode(i, count[i]));
            }
        }

        HuffmanNode one;
        HuffmanNode two;
        // combines nodes until only one is left
        while (pq.size() > 1) {
            one = pq.remove();
            two = pq.remove();
            pq.add(new HuffmanNode(-1, one.freq + two.freq, one, two));
        }
        // sets root to combined tree
        this.root = pq.remove();
    }

    /**
     * Constructor that takes in a scanner and constructs the object based on
     * it. Assumes that the scanner is not null, and the data is legitimate.
     *
     * @param codeInput
     *            the scanner to read in
     */
    public HuffmanCode(Scanner codeInput) {
        while (codeInput.hasNext()) {
            // start at the root everytime and follow the path to the node's
            // destination
            HuffmanNode current = this.root;
            int data = Integer.parseInt(codeInput.nextLine());
            String code = codeInput.nextLine();
            this.root = HuffmanCode.buildTree(current, data, code);
        }
    }

    /**
     * private recursive helper method to build the new node in the correct path
     *
     * @param current
     *            the node currently being operated on
     * @param data
     *            the ascii code of the character
     * @param code
     *            the path left to go
     * @return the changed version of the node being operated on
     */
    private static HuffmanNode buildTree(HuffmanNode current, int data, String code) {
        // base case, builds the node when end of path is reached
        if (code.isEmpty()) {
            current = new HuffmanNode(data, -1);
        } else {
            // if there is no node(path has not been reached before), build a
            // new default node(all non-leafs are default)
            if (current == null) {
                current = new HuffmanNode();
            }
            // follow the path and recurse
            if (code.charAt(0) == '0') {
                current.zero = HuffmanCode.buildTree(current.zero, data, code.substring(1));
            } else {
                current.one = HuffmanCode.buildTree(current.one, data, code.substring(1));
            }
        }
        return current;
    }

    /**
     * saves the current huffman code to the given output. Saves it in standard
     * format Assumes the PrintStream is not null.
     *
     * @param output
     *            the print stream to output to
     */
    public void save(PrintStream output) {
        this.save(output, this.root, "");

    }

    /**
     * private recusive helper method to help write out the tree. Recursively
     * traverses the tree and prints out the code in standard order.
     *
     * @param output
     *            the PrintStream to output to
     * @param current
     *            the node currently being operated on
     * @param accum
     *            the code to reach the current node
     */
    private void save(PrintStream output, HuffmanNode current, String accum) {
        if (current != null) {
            // if its a leaf/information carrying node
            if (current.one == null && current.zero == null) {
                output.println(current.data);
                output.println(accum);

            } else {
                // otherwise recurse
                this.save(output, current.zero, accum + "0");
                this.save(output, current.one, accum + "1");
            }
        }

    }

    /**
     * reads in BitInputStream, and translates it back into ASCII characters.
     * The translated data is output to a PrintStream. Assumes neither the input
     * not output are null
     *
     * @param input
     *            the BitInputStream to read
     * @param output
     *            the PrintStream to write the translated data to
     */
    public void translate(BitInputStream input, PrintStream output) {
        // not done recursively to make sure stack depth is not exceeded
        while (input.hasNextBit()) {
            HuffmanNode current = this.root;
            // while a leaf has not been reached
            while (current.zero != null && current.one != null) {
                if (input.nextBit() == 0) {
                    current = current.zero;
                } else {
                    current = current.one;
                }
            }
            // once leaf is reached, write the character reached
            output.write(current.data);
        }

    }
}
