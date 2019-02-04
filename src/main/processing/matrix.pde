import java.sql.Timestamp;    
import java.util.Date;
import java.math.BigInteger;

BufferedReader reader;
String line;
int timestamp;

void setup() {
  size(500, 500);
  frameRate(15);
  background(255);

  reader = createReader("G:\\downloads\\higgs-activity_time.csv");
  try {
    reader.readLine();
    } 
  catch (IOException e) {
    e.printStackTrace();
    line = null;
  }
  timestamp = 0;
}

int t;

void draw() {
  background(255);
  textSize(20);
  fill(0, 102, 153);

  stroke(0, 50);
  strokeWeight(2); 

  Date d = new Date();

  while (t <= timestamp) {
    try {
      line = reader.readLine();
      String[] interaction = split(line, " ");
      t = int(interaction[2]) - 1341100972;

      d = new Date(Long.parseLong(interaction[2]) * 1000);

      point(int(map(float(interaction[0]), 2, 456622, 0, width)), int(map(float(interaction[1]), 2, 456622, 0, height)));
    } 
    catch (IOException e) {
      e.printStackTrace();
      line = null;
    }
  }
 
  text(d.toString(), 100, 400);
 
  saveFrame("line-######.png");
  timestamp += 3600;
}
