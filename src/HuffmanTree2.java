import java.io.PrintStream;
import java.util.PriorityQueue;
import java.util.Scanner;

/**
 * A class that utilizes with Huffman trees.
 * 
 * @author dtabys
 *
 */
public class HuffmanTree2 {
	
	private HuffmanNode root;
	
	/**
	 * HuffmanNode is a node class for HuffmanTree.
	 * 
	 * @author dtabys
	 *
	 */
	private class HuffmanNode implements Comparable<HuffmanNode> {

		private int ascii;
		private int freq;
		private HuffmanNode l, r;
		
		/**
		 * Huffman node constructor given an ASCII code and the frequency of the character.
		 * 
		 * @param ascii
		 * 				Given ASCII code of a character.
		 * @param freq
		 * 				Given frequency of the character.
		 */
		private HuffmanNode(Integer ascii, Integer freq) {
			this.ascii = ascii;
			this.freq = freq;
		}

		/**
		 * Huffman node constructor using two other nodes.
		 * 
		 * @param l
		 * 			The left node of the the parent node.
		 * @param r
		 * 			The right node of the the parent node.
		 */
		private HuffmanNode(HuffmanNode l, HuffmanNode r) {
			this.ascii = -1;
			this.freq = l.freq + r.freq;
			this.l = l;
			this.r = r;
		}
		
		/**
		 * Checks if the node is a leaf.
		 * 
		 * @return
		 * 			TRUE if it is, FALSE if not.
		 */
		private boolean isLeaf() {
			return this.l == null;
		}
		
		/**
		 * Compares the nodes by frequency.
		 * 
		 */
		@Override
		public int compareTo(HuffmanNode h) {
			return this.freq - h.freq;
		}
		
	}
	
	/**
	 * Constructs a Huffman tree using the given array of frequencies.
	 * 
	 * @param count
	 * 				Is the number of occurrences of the character with ASCII value i.
	 */
	public HuffmanTree2(int[] count) {
		PriorityQueue<HuffmanNode> q = new PriorityQueue<HuffmanNode>();
		
		for (int i = 0; i < count.length; i ++) {
			if (count[i] > 0) {
				q.offer(new HuffmanNode(i, count[i]));
			}
		}
		
		q.offer(new HuffmanNode(count.length, 1));
		
		while (q.size() != 1) {
			q.offer(new HuffmanNode(q.poll(), q.poll()));
		}
		
		root = q.poll();
	}
	
	/**
	 * Constructs a Huffman tree from the Scanner.
	 * 
	 * @param input
	 * 				Assumes the Scanner contains a tree description in standard format.
	 */
	public HuffmanTree2(Scanner input) {
		root = new HuffmanNode(-1, -1);

		while (input.hasNextLine()) {
			int n = Integer.parseInt(input.nextLine());
			String[] code = input.nextLine().split("");
			HuffmanNode current = root;

			for (String i : code) {
				if (i.equals("0")) {
					if (current.l == null)
						current.l = new HuffmanNode(-1, -1);
					current = current.l;
				} else if (i.equals("1")) {
					if (current.r == null)
						current.r = new HuffmanNode(-1, -1);
					current = current.r;
				}
			}
			current.ascii = n;
		}
	}
	
	/**
	 * Constructs a Huffman tree from the given input stream.
	 * 
	 * @param input
	 * 				Assumes that the standard bit representation has been used for the tree.
	 */
	public HuffmanTree2(BitInputStream input) {
        root = rebuild(input);
	}
	
	/**
	 * Assigns codes for each character of the tree. 
	 * Fills in a String for each character in the tree indicating its code.
	 * 
	 * @param codes
	 * 				Assumes the array has null values before the method is called.
	 */
	public void assign(String[] codes) {
		traverse2(root, "", codes);
	}
	
	/**
	 * Writes the current tree to the output stream using the standard bit representation.
	 * 
	 * @param output
	 * 				Given output stream.
	 */
	public void writeHeader(BitOutputStream output) {
		traverse3(root, output);
	}
	
	/**
	 * Reads bits from the given input stream and writes the corresponding characters to the output.
	 * 
	 * @param input
	 * 			Assumes the input stream contains a legal encoding of characters for this tree’s Huffman code.
	 * @param output
	 * 			Given output stream.
	 * @param eof
	 * 			Pseudo-eof character. Stops reading when it encounters a character with value equal to eof.
	 */
	public void decode(BitInputStream input, PrintStream output, int eof) {
		while (true) {
			int bit;
			HuffmanNode current = root;
			while (!current.isLeaf()) {
				bit = input.readBit();
				if (bit == 0) {
					current = current.l;
				} else if (bit == 1) {
					current = current.r;
				}
			}
			if (current.ascii == eof) {
				break;
			}
			output.write(current.ascii);
		}
	}
	
	/**
	 * Writes the current tree to the given output stream in standard format.
	 * 
	 * @param output
	 * 				Given output stream.
	 */
	public void write(PrintStream output) {
		traverse(root, "", output);
	}
	
	/**
	 * Helper method to "write".
	 * Traverses the tree and prints the path and the character to the provided output stream.
	 * 
	 * @param tree
	 * 			Provided Huffman tree.
	 * @param path
	 * 			The record of a path taken.
	 * @param out
	 * 			Given output stream.
	 */
	private void traverse(HuffmanNode tree, String path, PrintStream out) {
		if (tree.isLeaf()){
			out.println(tree.ascii);
			out.println(path);
		} else {
			traverse(tree.l, path + "0", out);
			traverse(tree.r, path + "1", out);
		}
	}
	
	/** 
	 * Helper method for assign.
	 * 
	 * @param tree
	 * 			Current tree.
	 * @param path
	 * 			Path taken.
	 * @param codes
	 * 			Array to replace.
	 */
	private void traverse2(HuffmanNode tree, String path, String[] codes) {
		if (tree.isLeaf()){
			codes[tree.ascii]=path;
		} else {
			traverse2(tree.l, path + "0", codes);
			traverse2(tree.r, path + "1", codes);
		}
	}
	
	/**
	 * Helper method for writeHeader.
	 * 
	 * @param tree
	 * 			Current tree.
	 * @param out
	 * 			Given output stream.
	 */
	private void traverse3(HuffmanNode tree, BitOutputStream out) {
		if (tree.isLeaf()){
			out.writeBit(1);
			write9(out, tree.ascii);
		} else {
			out.writeBit(0);
			traverse3(tree.l, out);
			traverse3(tree.r, out);
		}
	}
	
	/**
	 * Helper method for HuffmanTree2(BitInputStream input).
	 * 
	 * @param input
	 * 			Given input stream.
	 * @return
	 * 		Returns a rebuilt tree from the header.
	 */
    private HuffmanNode rebuild(BitInputStream input){
        int bit = input.readBit();
        HuffmanNode node = new HuffmanNode(-1, -1);
        if (bit == 0){
            node.l = rebuild(input);
            node.r = rebuild(input);
        } else {
            node.ascii = read9(input);
        }
        return node;
    }
	
    /**
     * Provided methods below.
     */
    
	// pre : 0 <= n < 512
	// post: writes a 9-bit representation of n to the given output stream
	private void write9(BitOutputStream output, int n) {
	    for (int i = 0; i < 9; i++) {
	        output.writeBit(n % 2);
	        n /= 2;
	    }
	}
	 
	// pre : an integer n has been encoded using write9 or its equivalent
	// post: reads 9 bits to reconstruct the original integer
	private int read9(BitInputStream input) {
	    int multiplier = 1;
	    int sum = 0;
	    for (int i = 0; i < 9; i++) {
	        sum += multiplier * input.readBit();
	        multiplier *= 2;
	    }
	    return sum;
	}
	
}