public class TreeSerializeMain {

    public static void main(String[] args) {

        Node root = new Node(1);

        // Assigned all left node of the Binary tree
        root.left = new Node(2);
        root.left.right = new Node(5);
        root.left.left = new Node(7);
        root.left.left.left = new Node(4);

        root.right = new Node(1);
        root.right.right = new Node(28);

        // Note: The following line would introduce a cycle, creating a cyclic tree.
        //root.left.right = root.right;

        PreorderSerialize preorder = new PreorderSerialize();

        try{
            // Serialize the binary tree and print the serialized string.
            String result =  preorder.serialize(root);
            System.out.println("@@ Serialized Tree :: "+result);

            // Deserialize the serialized string back to a tree and print the serialized version of the deserialized tree.
            Node deserialize =  preorder.deserialize(result);
            System.out.println("@@ Deserialized root node :: "+ preorder.serialize(deserialize) );
        }
        catch(RuntimeException e){

            // Handle the case where a cyclic tree is detected during deserialization.
            System.out.println(":: "+e.getMessage());

        }
    }
}
