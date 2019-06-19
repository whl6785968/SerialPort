public class GetEncoding {
    public static void main(String[] args) {
        String property = System.getProperty("file.encoding");
        System.out.println("property = " + property);
    }
}
