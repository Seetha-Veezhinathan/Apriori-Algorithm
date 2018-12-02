import java.util.*;
import java.sql.*;

/*
 * APRIORI Algorithm Implementation 
 * This Class computes the frequent Item Set and
 * Displays only item sets which is greater than minimum support & confidence Values
 * 
 * @author SEETHA VEEZHINATHAN
 */

public class AprioriAlgorithm {

	public static void main(String args[]) throws ClassNotFoundException, SQLException
	{
		/* Declarations */
		HashMap<String,Float> list1_map = new HashMap<>();
		HashMap<String,Float> list2_map = new HashMap<>();
		HashMap<String,Float> list3_map = new HashMap<>();
		Scanner input = new Scanner(System.in);
		float min_support,min_confidence,support_val;
		int count = 0;
		int index = 1;
		
		/* Connecting to Oracle Database */
		String user = "*****";
		String password = "*****";
		String url = "jdbc:oracle:thin:@prophet.njit.edu:1521:course";		
		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection connection = DriverManager.getConnection(url,user,password);
		Statement statement = connection.createStatement();
		ResultSet resultset = null;		

		System.out.println("************************************************************************************************");
		System.out.println("			ASSOCIATION RULE GENERATION USING APRIORI ALGORITHM");
		System.out.println("************************************************************************************************");
		System.out.println("Enter the Minimum 'SUPPORT' value (in % format) :");
		min_support = input.nextInt();
		System.out.println("Enter the Minimum 'CONFIDENCE' value (in % format) :");
		min_confidence = input.nextInt();
		System.out.println("Select any one Table from the following 5 DataBase Tables\n\n");
		System.out.println("Transact_Table1 || Transact_Table2 ||  Transact_Table3 || Transact_Table4 || Transact_Table5");
		
		String table = input.next();
		if(table.equalsIgnoreCase("TRANSACT_TABLE1") || table.equalsIgnoreCase("TRANSACT_TABLE2") ||
				table.equalsIgnoreCase("TRANSACT_TABLE3") || table.equalsIgnoreCase("TRANSACT_TABLE4") ||
				table.equalsIgnoreCase("TRANSACT_TABLE5"))
		{
			/* Creating tables to store Frequent Item Sets */
			
			String createTable1 = "CREATE TABLE LIST1" + "(ID INTEGER NOT NULL, " + "ITEM1_NAME VARCHAR2(15), " +
									"SUPPORT_VALUE INTEGER, " + "PRIMARY KEY ( ID ))";
			statement.executeQuery(createTable1);
			
			String createTable2 = "CREATE TABLE LIST2" + "(ID INTEGER NOT NULL, " + "ITEM1_NAME VARCHAR2(15), " +
									"ITEM2_NAME VARCHAR2(15), " + "SUPPORT_VALUE INTEGER, " + "PRIMARY KEY(ID))";
			statement.executeQuery(createTable2);
			
			String createTable3 = "CREATE TABLE LIST3" + "(ID INTEGER NOT NULL, " + "ITEM1_NAME VARCHAR2(15), " +
									"ITEM2_NAME VARCHAR2(15), " + "ITEM3_NAME VARCHAR2(15), " + 
									"SUPPORT_VALUE INTEGER, " + "PRIMARY KEY(ID))";
			statement.executeQuery(createTable3);
			
			String createTable4 = "CREATE TABLE LIST4" + "(ID INTEGER NOT NULL, " + "ITEM1_NAME VARCHAR2(15), " +
									"ITEM2_NAME VARCHAR2(15), " + "ITEM3_NAME VARCHAR2(15), " + "ITEM4_NAME VARCHAR2(15), " + 
									"SUPPORT_VALUE INTEGER, " + "PRIMARY KEY(ID))";
			statement.executeQuery(createTable4);
			
			System.out.println("Apriori Algorithm has started...\n");
			System.out.println("Generating Association Rules for "+ table+ "\n\n");
			
			
			resultset = statement.executeQuery("SELECT COUNT(ITEM_ID) FROM MASTER_ITEM_TABLE");
			resultset.next();
			int total_item_count = resultset.getInt(1);
			resultset = null;
			System.out.println("Total Number of Items in Master Item Table :" +total_item_count);
			resultset = statement.executeQuery("SELECT COUNT(TRANS_ID) FROM "+ table);
			resultset.next();
			int total_trans_count = resultset.getInt(1);
			resultset = null;
			System.out.println("Total Number of Transactions in "+table+" is :" +total_trans_count+ "\n\n");
			
			/* Frequent 1 Item Set Calculation */
			ResultSet rs = statement.executeQuery("SELECT * FROM "+ table);		
			ResultSetMetaData rsmd = rs.getMetaData();
			int column_count = rsmd.getColumnCount();			
			for(int i=1; i<=total_item_count; i++)
			{
				ResultSet f_rs1 = statement.executeQuery("SELECT * FROM MASTER_ITEM_TABLE WHERE ITEM_ID ="+i);
				f_rs1.next();
				String f_itemName = f_rs1.getString(2);
				for(int j=1;j<=total_trans_count;j++)
				{
					ResultSet f_rs2 = statement.executeQuery("SELECT * FROM "+table+" WHERE TRANS_ID ="+j);
					f_rs2.next();
					for(int k=2;k<=column_count;k++)
					{
						String f_itemName1 = f_rs2.getString(k);
						if(f_itemName1.equals(f_itemName))
						{					
							count = count + 1;							
						}
					}					
				}
				support_val = count*100/total_trans_count;
				if(support_val>=min_support)
				{
					PreparedStatement ps_1 = connection.prepareStatement("INSERT INTO LIST1 VALUES (?,?,?)");
					ps_1.setInt(1,index);
					ps_1.setString(2,f_itemName);
					ps_1.setFloat(3, support_val);
					ps_1.executeQuery();
					list1_map.put(f_itemName, support_val);
					index++; 
				}
				count = 0;
			}
			
			/* Frequent 2 Item Set Calculation 	*/
			System.out.println("-----------------------------------------------------");
			System.out.println("	           FREQUENT 2 ITEM SETS");
			System.out.println("-----------------------------------------------------");
			ResultSet t_rs1 = statement.executeQuery("SELECT COUNT(ID) FROM LIST1");
			t_rs1.next();
			int list1_count = t_rs1.getInt(1);
			index = 1;
			int is = 0;
			for(int i=1;i<=list1_count;i++)
			{
				ResultSet t_rs2 = statement.executeQuery("SELECT * FROM LIST1 WHERE ID ="+i);
				t_rs2.next();
				String t_itemName1 = t_rs2.getString(2);
				for(int j=i+1;j<=list1_count;j++)
				{
					ResultSet t_rs3 = statement.executeQuery("SELECT * FROM LIST1 WHERE ID ="+j);
					t_rs3.next();
					String t_itemName2 = t_rs3.getString(2);
					for(int k=1;k<=total_trans_count;k++)
					{
						ResultSet t_rs4 = statement.executeQuery("SELECT * FROM "+table+" WHERE TRANS_ID ="+k);
						t_rs4.next();
						for(int a=2;a<=column_count;a++)
						{
							String t_itemName3 = t_rs4.getString(a);
							if(t_itemName3.equals(t_itemName1)){
								for(int b=2;b<=column_count;b++)
								{
									String t_itemName4 = t_rs4.getString(b);
									if(t_itemName4.equals(t_itemName2)){
										count = count + 1;
									}
								}
							}
						}
					}
					support_val = count*100/total_trans_count;
					if(support_val>=min_support)
					{
						PreparedStatement ps_2 = connection.prepareStatement("INSERT INTO LIST2 VALUES (?,?,?,?)");
						ps_2.setInt(1,index);
						ps_2.setString(2,t_itemName1);
						ps_2.setString(3,t_itemName2);
						ps_2.setFloat(4, support_val);
						ps_2.executeQuery();
						String Item2 = t_itemName1 + t_itemName2;
						list2_map.put(Item2, support_val);
						index++; 
					}
					count = 0;
					is++;
					System.out.println("ItemSet "+is+" ("+ t_itemName1+"," +t_itemName2+ ")");
				}					
			}
			
			/* Frequent 3 Item Set Calculation */	
			System.out.println("-----------------------------------------------------");
			System.out.println("	           FREQUENT 3 ITEM SETS");
			System.out.println("-----------------------------------------------------");
			ResultSet t_rs5 = statement.executeQuery("SELECT COUNT(ID) FROM LIST2");
			t_rs5.next();
			int list2_count = t_rs5.getInt(1);
			index = 1;
			is = 0;
			for(int i=1;i<=list2_count;i++)
			{
				ResultSet t_rs6 = statement.executeQuery("SELECT * FROM LIST2 WHERE ID ="+i);
				t_rs6.next();
				String r1_itemName1 = t_rs6.getString(2);
				String r1_itemName2 = t_rs6.getString(3);
				for(int j=i+1;j<=list2_count;j++)
				{
					ResultSet t_rs7 = statement.executeQuery("SELECT * FROM LIST2 WHERE ID ="+j);
					t_rs7.next();
					String r2_itemName1 = t_rs7.getString(2);
					String r2_itemName2 = t_rs7.getString(3);
					if(r1_itemName1.equals(r2_itemName1)){
						for(int k=1;k<=total_trans_count;k++)
						{
							ResultSet t_rs8 = statement.executeQuery("SELECT * FROM "+table+" WHERE TRANS_ID ="+k);
							t_rs8.next();	
							for(int a=2;a<=column_count;a++)
							{
								String Column_Item1 = t_rs8.getString(a);
								if(Column_Item1.equals(r1_itemName1)){
									for(int b=2;b<=column_count;b++)
									{
										String Column_Item2 = t_rs8.getString(b);
										if(Column_Item2.equals(r1_itemName2)){
											for(int c=2;c<=column_count;c++)
											{
												String Column_Item3 = t_rs8.getString(c);
												if(Column_Item3.equals(r2_itemName2)){							
													count = count + 1;
												}
											}
										}
									}
								}							
							}
						}
						support_val = count*100/total_trans_count;
						if(support_val>=min_support)
						{
							PreparedStatement ps_3 = connection.prepareStatement("INSERT INTO LIST3 VALUES (?,?,?,?,?)");
							ps_3.setInt(1,index);
							ps_3.setString(2,r1_itemName1);
							ps_3.setString(3,r1_itemName2);
							ps_3.setString(4,r2_itemName2);
							ps_3.setFloat(5, support_val);
							ps_3.executeQuery();
							String Item3 = r1_itemName1 + r1_itemName2 + r2_itemName2;
							list3_map.put(Item3, support_val);
							index++; 
						}
						count = 0;
						is++;
						System.out.println("ItemSet "+is+" ("+ r1_itemName1+"," +r1_itemName2+ ","+r2_itemName2+")");
					}
				}
			}
			
			/* Frequent 4 Item Set Calculation	*/
			System.out.println("-----------------------------------------------------");
			System.out.println("	           FREQUENT 4 ITEM SETS");
			System.out.println("-----------------------------------------------------");
			ResultSet t_rs9 = statement.executeQuery("SELECT COUNT(ID) FROM LIST3");
			t_rs9.next();
			int list3_count = t_rs9.getInt(1);
			index = 1;
			is = 0;
			for(int i=1;i<=list3_count;i++)
			{
				ResultSet t_rs10 = statement.executeQuery("SELECT * FROM LIST3 WHERE ID ="+i);
				t_rs10.next();
				String r1_itemName1 = t_rs10.getString(2);
				String r1_itemName2 = t_rs10.getString(3);
				String r1_itemName3 = t_rs10.getString(4);
				for(int j=i+1;j<=list3_count;j++)
				{
					ResultSet t_rs11 = statement.executeQuery("SELECT * FROM LIST3 WHERE ID ="+j);
					t_rs11.next();
					String r2_itemName1 = t_rs11.getString(2);
					String r2_itemName2 = t_rs11.getString(3);
					String r2_itemName3 = t_rs11.getString(4);
					if((r1_itemName1.equals(r2_itemName1)) && (r1_itemName2.equals(r2_itemName2))){
						for(int k=1;k<=total_trans_count;k++)
						{
							ResultSet t_rs12 = statement.executeQuery("SELECT * FROM "+table+" WHERE TRANS_ID ="+k);
							t_rs12.next();	
							for(int a=2;a<=column_count;a++)
							{
								String Column_Item1 = t_rs12.getString(a);
								if(Column_Item1.equals(r1_itemName1)){
									for(int b=2;b<=column_count;b++)
									{
										String Column_Item2 = t_rs12.getString(b);
										if(Column_Item2.equals(r1_itemName2)){
											for(int c=2;c<=column_count;c++)
											{
												String Column_Item3 = t_rs12.getString(c);
												if(Column_Item3.equals(r1_itemName3)){	
													for(int d=2;d<=column_count;d++)
													{
														String Column_Item4 = t_rs12.getString(d);
														if(Column_Item4.equals(r2_itemName3)){
															count = count + 1;
														}
													}
												}
											}
										}
									}							
								}
							}
						}
						support_val = count*100/total_trans_count;
						if(support_val>=min_support)
						{
							PreparedStatement ps_4 = connection.prepareStatement("INSERT INTO LIST4 VALUES (?,?,?,?,?,?)");
							ps_4.setInt(1,index);
							ps_4.setString(2,r1_itemName1);
							ps_4.setString(3,r1_itemName2);
							ps_4.setString(4,r1_itemName3);
							ps_4.setString(5,r2_itemName3);
							ps_4.setFloat(6, support_val);
							ps_4.executeQuery();
							index++; 
						}
						count = 0;
						is++;
						System.out.println("ItemSet "+is+" ("+ r1_itemName1+"," +r1_itemName2+ ","+r1_itemName3+ ","+r2_itemName3+")");
					}	
				}
			}		
			
			System.out.println("\n-------------------------------------------------------------------------");
			System.out.println("		           ASSOCIATION RULES					");
			System.out.println("-------------------------------------------------------------------------");
			System.out.println("\n-------------------------------------------------------------------------");
			System.out.println("|Rule_ID|              |Rule|              |Support,Confidence|");
			System.out.println("-------------------------------------------------------------------------\n\n\n");
			
			int ar = 1;
			/* Association Rule for 2 ITem Set */
			for (int i=1;i<=list2_count;i++)
			{
				ResultSet rs1 = statement.executeQuery("SELECT * FROM LIST2 WHERE ID = "+i);
				rs1.next();
				String A2_ItemName1 = rs1.getString(2);
				String A2_ItemName2 = rs1.getString(3);
				Float A2_SupportVal = rs1.getFloat(4);
				if(list1_map.containsKey(A2_ItemName1))
				{
					Float A1_SupportVal1 = list1_map.get(A2_ItemName1);
					Float Confidence_value1 = A2_SupportVal/A1_SupportVal1 *100;
					
					if(Confidence_value1 >= min_confidence)
					{
						System.out.println("Association Rule " +ar+ "  " + A2_ItemName1+ " ----> " +A2_ItemName2+ "("+A2_SupportVal+"%,"+Confidence_value1+"%)");
					}
				}				
				if(list1_map.containsKey(A2_ItemName2))
				{
					ar++;
					Float A1_SupportVal2 = list1_map.get(A2_ItemName2);
					Float Confidence_value2 = A2_SupportVal/A1_SupportVal2 *100;
					
					if(Confidence_value2 >= min_confidence)
					{
						System.out.println("Association Rule " +ar+ "  " + A2_ItemName2+ " ----> " +A2_ItemName1+ "("+A2_SupportVal+"%,"+Confidence_value2+"%)");
					}
				}
			}			
			
			/* Association Rule for 3 ITem Set */
			for (int i=1;i<=list3_count;i++)
			{
				ResultSet rs2 = statement.executeQuery("SELECT * FROM LIST3 WHERE ID = "+i);
				rs2.next();
				String A3_ItemName1 = rs2.getString(2);
				String A3_ItemName2 = rs2.getString(3);
				String A3_ItemName3 = rs2.getString(4);
				Float A3_SupportVal = rs2.getFloat(5);
				if(list1_map.containsKey(A3_ItemName1))
				{
					ar++;
					Float A1_SupportVal3 = list1_map.get(A3_ItemName1);
					Float Confidence_value3 = A3_SupportVal/A1_SupportVal3 *100;
					
					if(Confidence_value3 >= min_confidence)
					{
						System.out.println("Association Rule " +ar+ "  " + A3_ItemName1+ " ----> " +A3_ItemName2+ "," +A3_ItemName3+ "("+A3_SupportVal+"%,"+Confidence_value3+"%)");
					}
				}				
				if(list1_map.containsKey(A3_ItemName2))
				{
					ar++;
					Float A1_SupportVal4 = list1_map.get(A3_ItemName2);
					Float Confidence_value4 = A3_SupportVal/A1_SupportVal4 *100;
					
					if(Confidence_value4 >= min_confidence)
					{
						System.out.println("Association Rule " +ar+ "  " + A3_ItemName2+ " ----> " +A3_ItemName1+ "," +A3_ItemName3+ "("+A3_SupportVal+"%,"+Confidence_value4+"%)");
					}
				}				
				if(list1_map.containsKey(A3_ItemName3))
				{
					ar++;
					Float A1_SupportVal5 = list1_map.get(A3_ItemName3);
					Float Confidence_value5 = A3_SupportVal/A1_SupportVal5 *100;
					
					if(Confidence_value5 >= min_confidence)
					{
						System.out.println("Association Rule " +ar+ "  " + A3_ItemName3+ " ----> " +A3_ItemName1+ "," +A3_ItemName2+ "("+A3_SupportVal+"%,"+Confidence_value5+"%)");
					}
				}
			}
			
			for (int i=1;i<=list3_count;i++)
			{
				ResultSet rs3 = statement.executeQuery("SELECT * FROM LIST3 WHERE ID = "+i);
				rs3.next();
				String A3_ItemName4 = rs3.getString(2);
				String A3_ItemName5 = rs3.getString(3);
				String A3_ItemName6 = rs3.getString(4);
				Float A3_SupportVal1 = rs3.getFloat(5);				
				String Item_group1 = A3_ItemName5 + A3_ItemName6;
				String Item_group2 = A3_ItemName4 + A3_ItemName6;
				String Item_group3 = A3_ItemName4 + A3_ItemName5;				
				if(list2_map.containsKey(Item_group1))
				{
					ar++;
					Float A1_SupportVal6 = list2_map.get(Item_group1);
					Float Confidence_value6 = A3_SupportVal1/A1_SupportVal6 *100;
					
					if(Confidence_value6 >= min_confidence)
					{
						System.out.println("Association Rule " +ar+ "  " + A3_ItemName5+ "," +A3_ItemName6+ " ----> " +A3_ItemName4+ "("+A3_SupportVal1+"%,"+Confidence_value6+"%)");
					}
				}				
				if(list2_map.containsKey(Item_group2))
				{
					ar++;
					Float A1_SupportVal7 = list2_map.get(Item_group2);
					Float Confidence_value7 = A3_SupportVal1/A1_SupportVal7 *100;
					
					if(Confidence_value7 >= min_confidence)
					{
						System.out.println("Association Rule " +ar+ "  " + A3_ItemName4+ "," +A3_ItemName6+ " ----> " +A3_ItemName5+ "("+A3_SupportVal1+"%,"+Confidence_value7+"%)");
					}
				}				
				if(list2_map.containsKey(Item_group3))
				{
					ar++;
					Float A1_SupportVal8 = list2_map.get(Item_group3);
					Float Confidence_value8 = A3_SupportVal1/A1_SupportVal8 *100;
					
					if(Confidence_value8 >= min_confidence)
					{
						System.out.println("Association Rule " +ar+ "  " + A3_ItemName4+ "," +A3_ItemName5+ " ----> " +A3_ItemName6+ "("+A3_SupportVal1+"%,"+Confidence_value8+"%)");
					}
				}
			}
			
			/* Association Rule for 4 ITem Set */
			
			rs = statement.executeQuery("SELECT COUNT(ID) FROM LIST4");
			rs.next();
			int list4_count = rs.getInt(1);			
			for (int i=1;i<=list4_count;i++)
			{
				ResultSet rs4 = statement.executeQuery("SELECT * FROM LIST4 WHERE ID = "+i);
				rs4.next();
				String A4_ItemName1 = rs4.getString(2);
				String A4_ItemName2 = rs4.getString(3);
				String A4_ItemName3 = rs4.getString(4);
				String A4_ItemName4 = rs4.getString(5);
				Float A4_SupportVal = rs4.getFloat(6);				
				if(list1_map.containsKey(A4_ItemName1))
				{
					ar++;
					Float A1_SupportVal9 = list1_map.get(A4_ItemName1);
					Float Confidence_value9 = A4_SupportVal/A1_SupportVal9 *100;
					
					if(Confidence_value9 >= min_confidence)
					{
						System.out.println("Association Rule " +ar+ "  " + A4_ItemName1+ " ----> " +A4_ItemName2+ "," +A4_ItemName3+ "," +A4_ItemName4+ "("+A4_SupportVal+"%,"+Confidence_value9+"%)");
					}
				}				
				if(list1_map.containsKey(A4_ItemName2))
				{
					ar++;
					Float A1_SupportVal10 = list1_map.get(A4_ItemName2);
					Float Confidence_value10 = A4_SupportVal/A1_SupportVal10 *100;
					
					if(Confidence_value10 >= min_confidence)
					{
						System.out.println("Association Rule " +ar+ "  " + A4_ItemName2+ " ----> " +A4_ItemName1+ "," +A4_ItemName3+ "," +A4_ItemName4+ "("+A4_SupportVal+"%,"+Confidence_value10+"%)");
					}
				}				
				if(list1_map.containsKey(A4_ItemName3))
				{
					ar++;
					Float A1_SupportVal11 = list1_map.get(A4_ItemName3);
					Float Confidence_value11 = A4_SupportVal/A1_SupportVal11 *100;
					
					if(Confidence_value11 >= min_confidence)
					{
						System.out.println("Association Rule " +ar+ "  " + A4_ItemName3+ " ----> " +A4_ItemName1+ "," +A4_ItemName2+ "," +A4_ItemName4+ "("+A4_SupportVal+"%,"+Confidence_value11+"%)");
					}
				}				
				if(list1_map.containsKey(A4_ItemName4))
				{
					ar++;
					Float A1_SupportVal12 = list1_map.get(A4_ItemName4);
					Float Confidence_value12 = A4_SupportVal/A1_SupportVal12 *100;
					
					if(Confidence_value12 >= min_confidence)
					{
						System.out.println("Association Rule " +ar+ "  " + A4_ItemName4+ " ----> " +A4_ItemName1+ "," +A4_ItemName2+ "," +A4_ItemName3+ "("+A4_SupportVal+"%,"+Confidence_value12+"%)");
					}
				}
			}
			
			/* Association Rules for Combination of 3 Values from 4 ItemSet */
			for (int i=1;i<=list4_count;i++)
			{
				ResultSet rs5 = statement.executeQuery("SELECT * FROM LIST4 WHERE ID = "+i);
				rs5.next();
				String A4_ItemName5 = rs5.getString(2);
				String A4_ItemName6 = rs5.getString(3);
				String A4_ItemName7 = rs5.getString(4);
				String A4_ItemName8 = rs5.getString(5);
				Float A4_SupportVal1 = rs5.getFloat(6);				
				String Item_group4 = A4_ItemName6 + A4_ItemName7 + A4_ItemName8;
				String Item_group5 = A4_ItemName5 + A4_ItemName7 + A4_ItemName8;
				String Item_group6 = A4_ItemName5 + A4_ItemName6 + A4_ItemName8;
				String Item_group7 = A4_ItemName5 + A4_ItemName6 + A4_ItemName7;				
				if(list3_map.containsKey(Item_group4))
				{
					ar++;
					Float A1_SupportVal13 = list3_map.get(Item_group4);
					Float Confidence_value13 = A4_SupportVal1/A1_SupportVal13 *100;
					
					if(Confidence_value13 >= min_confidence)
					{
						System.out.println("Association Rule " +ar+ "  " +A4_ItemName6+ "," +A4_ItemName7+ "," +A4_ItemName8+  " ----> " + A4_ItemName5+  "("+A4_SupportVal1+"%,"+Confidence_value13+"%)");
					}
				}				
				if(list3_map.containsKey(Item_group5))
				{
					ar++;
					Float A1_SupportVal14 = list3_map.get(Item_group5);
					Float Confidence_value14 = A4_SupportVal1/A1_SupportVal14 *100;
					
					if(Confidence_value14 >= min_confidence)
					{
						System.out.println("Association Rule " +ar+ "  " +A4_ItemName5+ "," +A4_ItemName7+ "," +A4_ItemName8+  " ----> " + A4_ItemName6+  "("+A4_SupportVal1+"%,"+Confidence_value14+"%)");
					}
				}				
				if(list3_map.containsKey(Item_group6))
				{
					ar++;
					Float A1_SupportVal15 = list3_map.get(Item_group6);
					Float Confidence_value15 = A4_SupportVal1/A1_SupportVal15 *100;
					
					if(Confidence_value15 >= min_confidence)
					{
						System.out.println("Association Rule " +ar+ "  " +A4_ItemName5+ "," +A4_ItemName6+ "," +A4_ItemName8+  " ----> " + A4_ItemName7+  "("+A4_SupportVal1+"%,"+Confidence_value15+"%)");
					}
				}				
				if(list3_map.containsKey(Item_group7))
				{
					ar++;
					Float A1_SupportVal16 = list3_map.get(Item_group7);
					Float Confidence_value16 = A4_SupportVal1/A1_SupportVal16 *100;
					
					if(Confidence_value16 >= min_confidence)
					{
						System.out.println("Association Rule " +ar+ "  " +A4_ItemName5+ "," +A4_ItemName6+ "," +A4_ItemName7+  " ----> " + A4_ItemName8+  "("+A4_SupportVal1+"%,"+Confidence_value16+"%)");
					}
				}
			} 
			
			/* Association Rules for Combination of 2 Values from 4 ItemSet */
			for (int i=1;i<=list4_count;i++)
			{
				ResultSet rs6 = statement.executeQuery("SELECT * FROM LIST4 WHERE ID = "+i);
				rs6.next();
				String A4_ItemName9 = rs6.getString(2);
				String A4_ItemName10 = rs6.getString(3);
				String A4_ItemName11 = rs6.getString(4);
				String A4_ItemName12 = rs6.getString(5);
				Float A4_SupportVal2 = rs6.getFloat(6);				
				String Item_group8 = A4_ItemName9 + A4_ItemName10;
				String Item_group9 = A4_ItemName11 + A4_ItemName12;
				String Item_group10 = A4_ItemName9 + A4_ItemName11;
				String Item_group11 = A4_ItemName10 + A4_ItemName12;
				String Item_group12 = A4_ItemName9 + A4_ItemName12;
				String Item_group13 = A4_ItemName10 + A4_ItemName11;				
				if(list2_map.containsKey(Item_group8))
				{
					ar++;
					Float A1_SupportVal17 = list2_map.get(Item_group8);
					Float Confidence_value17 = A4_SupportVal2/A1_SupportVal17 *100;
					
					if(Confidence_value17 >= min_confidence)
					{
						System.out.println("Association Rule " +ar+ "  " +A4_ItemName9+ "," +A4_ItemName10+ " ----> " +A4_ItemName11+ "," +A4_ItemName12+  "("+A4_SupportVal2+"%,"+Confidence_value17+"%)");
					}
				}				
				if(list2_map.containsKey(Item_group9))
				{
					ar++;
					Float A1_SupportVal18 = list2_map.get(Item_group9);
					Float Confidence_value18 = A4_SupportVal2/A1_SupportVal18 *100;
					
					if(Confidence_value18 >= min_confidence)
					{
						System.out.println("Association Rule " +ar+ "  " +A4_ItemName11+ "," +A4_ItemName12+ " ----> " +A4_ItemName9+ "," +A4_ItemName10+  "("+A4_SupportVal2+"%,"+Confidence_value18+"%)");
					}
				}				
				if(list2_map.containsKey(Item_group10))
				{
					ar++;
					Float A1_SupportVal19 = list2_map.get(Item_group10);
					Float Confidence_value19 = A4_SupportVal2/A1_SupportVal19 *100;
					
					if(Confidence_value19 >= min_confidence)
					{
						System.out.println("Association Rule " +ar+ "  " +A4_ItemName9+ "," +A4_ItemName11+ " ----> " +A4_ItemName10+ "," +A4_ItemName12+  "("+A4_SupportVal2+"%,"+Confidence_value19+"%)");
					}
				}				
				if(list2_map.containsKey(Item_group11))
				{
					ar++;
					Float A1_SupportVal20 = list2_map.get(Item_group11);
					Float Confidence_value20 = A4_SupportVal2/A1_SupportVal20 *100;
					
					if(Confidence_value20 >= min_confidence)
					{
						System.out.println("Association Rule " +ar+ "  " +A4_ItemName10+ "," +A4_ItemName12+ " ----> " +A4_ItemName9+ "," +A4_ItemName11+  "("+A4_SupportVal2+"%,"+Confidence_value20+"%)");
					}
				}				
				if(list2_map.containsKey(Item_group12))
				{
					ar++;
					Float A1_SupportVal21 = list2_map.get(Item_group12);
					Float Confidence_value21 = A4_SupportVal2/A1_SupportVal21 *100;
					
					if(Confidence_value21 >= min_confidence)
					{
						System.out.println("Association Rule " +ar+ "  " +A4_ItemName9+ "," +A4_ItemName12+ " ----> " +A4_ItemName10+ "," +A4_ItemName11+  "("+A4_SupportVal2+"%,"+Confidence_value21+"%)");
					}
				}				
				if(list2_map.containsKey(Item_group13))
				{
					ar++;
					Float A1_SupportVal22 = list2_map.get(Item_group13);
					Float Confidence_value22 = A4_SupportVal2/A1_SupportVal22 *100;
					
					if(Confidence_value22 >= min_confidence)
					{
						System.out.println("Association Rule " +ar+ "  " +A4_ItemName10+ "," +A4_ItemName11+ " ----> " +A4_ItemName9+ "," +A4_ItemName12+  "("+A4_SupportVal2+"%,"+Confidence_value22+"%)");
					}
				}
			}			
			PreparedStatement pst1 = connection.prepareStatement("DROP TABLE LIST1");
			pst1.executeQuery();			
			PreparedStatement pst2 = connection.prepareStatement("DROP TABLE LIST2");
			pst2.executeQuery();			
			PreparedStatement pst3 = connection.prepareStatement("DROP TABLE LIST3");
			pst3.executeQuery();			
			PreparedStatement pst4 = connection.prepareStatement("DROP TABLE LIST4");
			pst4.executeQuery();		
			connection.close();
		}
		else
		{
			System.out.println("Table '" +table+ "' does not exist.Provide Correct Table Name as mentioned in the list.");
		}
	}
}
