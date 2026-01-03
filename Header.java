public class Header {
      private final String name;
      private final String value;

      public Header(String name, String value){
            this.name = name;
            this.value = value;
      }

      public String toHttpString(){
            return name + ": " + value + "\r\n";
      }
}