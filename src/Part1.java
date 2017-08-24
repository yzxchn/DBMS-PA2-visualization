import java.sql.*;
import java.util.*;
import edu.brandeis.cs127b.pa2.gnuplot.*;
public class Part1 {
	static final String JDBC_DRIVER = "com.postgresql.jdbc.Driver";
	static final String DB_TYPE = "postgresql";
	static final String DB_DRIVER = "jdbc";
	static final String DB_NAME = System.getenv("PGDATABASE");
	static final String DB_HOST = System.getenv("PGHOST");
	static final String DB_URL = String.format("%s:%s://%s/%s",DB_DRIVER, DB_TYPE, DB_HOST, DB_NAME);
	static final String DB_USER = System.getenv("PGUSER");
	static final String DB_PASSWORD = System.getenv("PGPASSWORD");
	static Connection conn;


        static final String REGION_QUERY = "SELECT r_regionkey, r_name FROM region ";
        static final String SALES_QUERY = "SELECT SUM(l_extendedprice) AS sales,"+
                                                  "DATE_TRUNC('month', l_shipdate) "+
                                                  "AS year_month "+
                                          "FROM (lineitem JOIN "+
                                          "(SELECT s_suppkey AS z_suppkey FROM "+
                                          "supplier JOIN nation ON (s_nationkey=n_nationkey) "+
                                          "WHERE n_regionkey=%d) "+
                                          "AS supp_in_region ON (l_suppkey=z_suppkey)) "+
                                          "GROUP BY DATE_TRUNC('month', l_shipdate) "+ 
                                          "ORDER BY DATE_TRUNC('month', l_shipdate);";

	public static void main(String[] args) throws SQLException {
		conn = DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWORD);
                final String title = "Supplier Sales by Region";
                final String xlabel = "Year";
                final String ylabel = "Sales(Thousands)";
		TimeSeriesPlot plot = new TimeSeriesPlot(title, xlabel, ylabel);
		Statement st = conn.createStatement();
                ResultSet regions = st.executeQuery(REGION_QUERY);
                HashMap<Integer, String> regionMap = new HashMap<>();
                ArrayList<Integer> regionkeys = new ArrayList<>();
                // create a map from region keys to region names
                while (regions.next()){
                    int k = regions.getInt("r_regionkey");
                    String n = regions.getString("r_name");
                    regionkeys.add(k);
                    regionMap.put(k, n);
                }
                // for each region, find out the total sales over time
                for (int rk: regionkeys){
                    Statement salesS = conn.createStatement();
                    // group the sales over time by months and sum them up
                    ResultSet salesInRegion = salesS.executeQuery(String.format(SALES_QUERY, rk));
                    DateLine l = new DateLine(regionMap.get(rk));
                    // for each month, add a point to the plot
                    while (salesInRegion.next()){
                        double sales = salesInRegion.getFloat("sales")/1000;
                        java.util.Date yearMonth = salesInRegion.getTimestamp("year_month");
                        DatePoint p = new DatePoint(yearMonth, sales);
                        l.add(p);
                    }
                    plot.add(l);
                }
                System.out.println(plot);
	}

}
