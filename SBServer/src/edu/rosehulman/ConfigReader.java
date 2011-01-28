package edu.rosehulman;

import java.io.File;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ConfigReader {
	
	private File file;
	private NodeList nodes;

	
	public ConfigReader(String filename){
		file = new File("config.xml");
		if (filename == null || !filename.equals(""))
			file = new File(filename);
		
		  DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		  DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);
			doc.getDocumentElement().normalize();
			nodes = doc.getElementsByTagName("user");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<Client> getClients(){
		ArrayList<Client> result = new ArrayList<Client>();
		String name, ip;
		Channel chan;
		for (int i = 0; i <= nodes.getLength(); i++) {
			Node fstNode = nodes.item(i);
			
			 if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
				  
		          Element fstElmnt = (Element) fstNode;
			      NodeList fstNmElmntLst = fstElmnt.getElementsByTagName("ip_address");
			      Element fstNmElmnt = (Element) fstNmElmntLst.item(0);
			      NodeList fstNm = fstNmElmnt.getChildNodes();
			      ip = ((Node) fstNm.item(0)).getNodeValue();
			      System.out.println("IP Address : "  + ip);
			      
			      NodeList lstNmElmntLst = fstElmnt.getElementsByTagName("comp_name");
			      Element lstNmElmnt = (Element) lstNmElmntLst.item(0);
			      NodeList lstNm = lstNmElmnt.getChildNodes();
			      name = ((Node) lstNm.item(0)).getNodeValue();
			      System.out.println("Computer Name : " + name);
			      
			      chan = new Channel();
			      
			      NodeList audienceLst = fstElmnt.getElementsByTagName("audience");
			      Element audienceElmnt = (Element) audienceLst.item(0);
			      NodeList audience = audienceElmnt.getChildNodes();
			      
			      for (int j = 0; j <= audience.getLength(); j++)
			      {
				      NodeList audienceIp = fstElmnt.getElementsByTagName("ip_address");
				      Element audienceElement = (Element) audienceIp.item(j);
				      NodeList audienceList = audienceElement.getChildNodes();
				      String audIp = ((Node) audienceList.item(j)).getNodeValue();
				      System.out.println("IP Address : "  + audIp);
				      
				      NodeList audienceComp = fstElmnt.getElementsByTagName("comp_name");
				      Element audienceCompEle = (Element) audienceComp.item(j);
				      NodeList audCompLst = audienceCompEle.getChildNodes();
				      String audCname = ((Node) audCompLst.item(j)).getNodeValue();
				      System.out.println("Computer Name : " + name);
				      chan.add(new Client(audIp, audCname));
				      
			      }
			      result.add(new Client(chan, name, ip));
			 }
		}
		return result;
	}
}
