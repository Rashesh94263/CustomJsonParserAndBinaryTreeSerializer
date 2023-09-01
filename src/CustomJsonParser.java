import java.util.*;


public class CustomJsonParser {

    // Method for Json Parser to parse Json string in to Map<String, Object> type.
    public static Map<String, Object> parse(String json) {

        Stack<Map<String, Object>> objectStack = new Stack<>();
        Map<String, Object> parsedObject = new HashMap<>();
        boolean parsingKey = false, parsingValue = false, flagValue = false;
        StringBuilder currentKey = new StringBuilder();
        List<String> currentValue = new ArrayList<>();

        char stringQuote = '"';

        for (int i = 0; i < json.length(); i++) {
            char character = json.charAt(i);
            // Check for opening curly brace '{' and handle object parsing
            if (character == '{' && !flagValue) {
                if (parsingValue) {
                    objectStack.push(parsedObject);
                    Map<String, Object> newObject = new HashMap<>();
                    parsedObject.put(currentKey.toString(), newObject);

                    currentKey.setLength(0);
                    parsedObject = newObject;

                    parsingValue = false;
                } else {
                    objectStack.push(parsedObject);
                }


            } else if (character == '}' && !flagValue) {
                if (parsingValue) {

                    // Store the current value and reset for next parsing
                    parsedObject.put(currentKey.toString(), parseDatatype(String.join("",currentValue)));

                    currentValue.clear();
                    parsingValue = false;
                }
                if (!objectStack.isEmpty()) {
                    parsedObject = objectStack.pop();
                }

                // Check for colon ':' to switch from key to value parsing
            } else if (character == ':') {
                parsingKey = false;
                parsingValue = true;

                // Check for comma ',' to finalize value parsing
            } else if (character == ',') {
                if (parsingValue && !flagValue) {
                    parsedObject.put(currentKey.toString(), parseDatatype(String.join("", currentValue)));
                    //System.out.println("@@ , :: "+currentKey);
                    currentKey.setLength(0);
                    currentValue.clear();
                    parsingValue = false;
                }

            }
            else if (character == '[' && !flagValue) {
                List<Object> arrayItems = new ArrayList<>();
                StringBuilder arrayItem = new StringBuilder();

                int nestedLevel = 1;
                i++; // Move to the next character after '['

                while (i < json.length() && nestedLevel > 0) {
                    char arrayChar = json.charAt(i);

                    if (arrayChar == '[') {
                        nestedLevel++;
                    } else if (arrayChar == ']') {
                        nestedLevel--;
                    }

                    if (nestedLevel > 0) {
                        arrayItem.append(arrayChar);
                    }

                    if (arrayChar == ',' && nestedLevel == 1) {
                        arrayItems.add(parseDatatype(arrayItem.toString().trim()));
                        arrayItem.setLength(0);
                    }

                    i++;
                }

                if (arrayItem.length() > 0) {
                    arrayItems.add(parseDatatype(arrayItem.toString().trim()));
                }

                // System.out.println("##arrayItems:: "+arrayItems);
                parsedObject.put(currentKey.toString(), arrayItems);
                // System.out.println("@@currentKey:: "+currentKey);
                parsingValue = false;

            } else if (character == '"' || character == '\'') {
                if (flagValue && character == stringQuote) {
                    flagValue = false;
                } else {
                    flagValue = true;
                    stringQuote = character;
                }

                // Handle parsing of values
            } else if (parsingValue) {
                currentValue.add(Character.toString(character));
            } else if (parsingKey) {
                currentKey.append(character);
                //System.out.println("@@parsingKey:: "+currentKey);

            } else {
                // Start parsing the key
                parsingKey = true;
                currentKey.append(character);

            }
        }

        return parsedObject;
    }


    //Method to parse String datatype to Object
    private static Object parseDatatype(String data) {

        if (data.startsWith("\"") && data.endsWith("\"")) {
            return  unicodeStringValue(data.substring(1, data.length()-1));
        } else if (data.equalsIgnoreCase("true") || data.equalsIgnoreCase("false")) {
            return Boolean.parseBoolean(data);
        } else if (data.equalsIgnoreCase("null")) {
            return null;

        } else if (data.startsWith("[") && data.endsWith("]")) {

            // parse simple list elements here
            String[] elements = data.substring(1, data.length() - 1).split(",");
            List<Object> objectArrayList = new ArrayList<>();

            for (String element : elements) {
                objectArrayList.add(parseDatatype(element.trim()));

            }

            return objectArrayList;


        } else if (data.matches("^-?\\d+(\\.\\d+)?([eE][-+]?\\d+)?$")) {

            if (data.contains(".") || data.toLowerCase().contains("e")) {
                return Double.parseDouble(data);
            } else {
                try {

                    if (data.matches("^-?\\d+$")) {
                        long longValue = Long.parseLong(data);

                        // Check if it's within the valid Integer range
                        if (longValue >= Integer.MIN_VALUE && longValue <= Integer.MAX_VALUE) {
                            return Integer.parseInt(data);
                        } else {
                            return longValue;

                        }
                    } else {
                        return Long.parseLong(data);
                    }
                }
                catch (NumberFormatException e){

                    return Long.parseLong(data);
                }
            }
        }

        return data;

    }

    // Method to convert JSON escape sequences to corresponding characters
    private static  String unicodeStringValue(String value) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '\\' && i + 1 < value.length()) {
                char nextChar = value.charAt(i + 1);
                switch (nextChar) {
                    case '"','\\','/','\''->{
                        result.append(nextChar);
                        i++;
                    }
                    case 'b' -> {
                        result.append('\b');
                        i++;
                    }
                    case 'f' -> {
                        result.append('\f');
                        i++;
                    }
                    case 'n' -> {
                        result.append('\n');
                        i++;
                    }
                    case 'r' -> {
                        result.append('\r');
                        i++;
                    }
                    case 't' -> {
                        result.append('\t');
                        i++;
                    }
                    case 'u' -> {
                        if (i + 5 < value.length()) {
                            String fourHexDigits = value.substring(i + 2, i + 6);
                            try {
                                int unicode = Integer.parseInt(fourHexDigits, 16);
                                result.append((char) unicode);
                                i += 5; // Skip the Unicode sequence
                            } catch (NumberFormatException e) {
                                // Invalid Unicode escape, treat it as a regular character
                                result.append(c);
                            }
                        } else {
                            // Not enough characters for a valid Unicode escape, treat it as a regular character
                            result.append(c);
                        }
                    }
                    default ->
                        // Invalid escape sequence, treat the backslash as a regular character
                            result.append(c);
                }
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }



    public static  void main(String[] args) {
        String json = "{\"user\":{\"name\":\"John Doe\",\"age\":30,\"address\":{\"street\":\"123 Main Street\",\"city\":\"Los Angeles\",\"state\":\"CA\",\"zipcode\":90815}},\"orders\":[{\"order_id\":123456,\"product_name\":\"iPhone 15 Pro\",\"quantity\":1,\"price\":1199.99},{\"order_id\":789012,\"product_name\":\"MacBook Pro\",\"quantity\":2,\"price\":1999.99},{\"order_id\":null,\"product_name\":null,\"quantity\":null,\"price\":null},{\"order_id\":\"unused character\",\"product_name\":\"unused character\",\"quantity\":\"unused character\",\"price\":\"unused character\"}]}";

        Map<String, Object> output = parse(json);

        System.out.println("output:: "+output);

    }
}

