import java.sql.*;
import edu.brandeis.cs127b.pa2.latex.*;
import java.util.Map;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.Scanner;
import java.util.Set;
public class Part3 {
	static final String JDBC_DRIVER = "com.postgresql.jdbc.Driver";
	static final String DB_TYPE = "postgresql";
	static final String DB_DRIVER = "jdbc";
	static final String DB_NAME = System.getenv("PGDATABASE");
	static final String DB_HOST = System.getenv("PGHOST");
	static final String DB_URL = String.format("%s:%s://%s/%s",DB_DRIVER, DB_TYPE, DB_HOST, DB_NAME);
	static final String DB_USER = System.getenv("PGUSER");
	static final String DB_PASSWORD = System.getenv("PGPASSWORD");
	static Connection conn;

        static final String MIN_SUPP_QUERY = "SELECT ps_suppkey, ps_supplycost FROM partsupp WHERE ps_partkey=%s ORDER BY ps_supplycost;";

	public static void main(String[] args) throws SQLException{
		conn = DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWORD);
		Scanner in = new Scanner(System.in);
		Document doc = new Document();
                // For each order, construct the purchage document
		while (in.hasNextLine()){
			String[] arr = in.nextLine().split(":");
			String purchaseNumber = arr[0];
                        String[] partQuantities = arr[1].split(",");
			Map<String, Set<Part>> suppliers = new HashMap<String,Set<Part>>();
                        // For each part listed in the order, find out the supplier that has the cheapest part
                        for (String pq: partQuantities){
                            String[] partQuantity = pq.split("x");
                            String partNum = partQuantity[1];
                            int quantity = Integer.parseInt(partQuantity[0]);
                            Part part = new Part(partNum,quantity);
                            Statement st = conn.createStatement();
                            ResultSet rs = st.executeQuery(String.format(MIN_SUPP_QUERY, partNum));
                            if (rs.next()){
                                String suppNum = rs.getString(1);
                                double partCost = rs.getDouble(2);
                                part.setCost(partCost);
                                if (!suppliers.containsKey(suppNum)){
                                    TreeSet<Part> parts = new TreeSet<Part>();
                                    suppliers.put(suppNum, parts);
                                }
                                suppliers.get(suppNum).add(part);
                            }
                        }
			Purchase p = new Purchase(purchaseNumber);
			for (String suppNum : suppliers.keySet()){
                                Supplier supp = new Supplier(suppNum);
				Suborder o = new Suborder(supp);
				p.add(o);
				for (Part part : suppliers.get(suppNum)){
                                    o.add(part);
				}
			}
			doc.add(p);
		}	
		System.out.println(doc);
	}

}
