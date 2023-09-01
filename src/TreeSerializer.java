import java.util.*;

public interface TreeSerializer {

    String serialize(Node root);
    Node deserialize(String str);
}

class PreorderSerialize implements TreeSerializer{


        int index =0;
        @Override
        public String serialize(Node root){

            List<String> serializeStringList = new ArrayList<>();
            // Map to track visited nodes during serialization.
            Map<Node , String> visitedNode = new HashMap<>();


            if(root == null){
                throw new RuntimeException("Tree is empty");
            }

            // Perform serialization using DFS.
            serializePreorderDFS(root,serializeStringList, visitedNode );

            return String.join("->",serializeStringList) ;
        }

       // Recursive method to perform preorder serialization using DFS.
       private void serializePreorderDFS(Node root, List<String> serializeStringList, Map<Node,String> visitedNode){

                if (root == null){
                    serializeStringList.add("null"); // added "null" value for null nodes.
                    return;
                }

                if(visitedNode.containsKey(root)){
                    throw new RuntimeException("Tree is Cycle:: ");
                }

                visitedNode.put(root,String.valueOf(root.value));

                serializeStringList.add(String.valueOf(root.value));
                //System.out.println("root.val:: "+root.value);
                serializePreorderDFS(root.left,serializeStringList,visitedNode);
                serializePreorderDFS(root.right,serializeStringList,visitedNode);

        }

        @Override
        public Node deserialize(String serializeString){

            String[] serializeValueArray = serializeString.split("->");

            // Perform deserialization using DFS and return the root node.
            return deserializeDFS(serializeValueArray);
        }

      // Recursive method to perform preorder deserialization using DFS.
       private Node deserializeDFS(String[] serializeArray){

                String serializeValue = serializeArray[index];

                if(serializeValue.equals("null")){
                    index++;
                    return null;
                }

                Node node = new Node( Integer.parseInt(serializeValue) );

                // System.out.println("index::" + index+"values:: "+ serializeValue);
                index++;

                // Recursive call to add left subtree
                node.left = deserializeDFS(serializeArray);

                // Recursive call to add right subtree
                node.right = deserializeDFS(serializeArray);

                return node;
        }
}
