//package org.spt.compiler;
//
//import org.apache.xerces.impl.xs.opti.NodeImpl;
//import org.w3c.dom.Document;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
//import org.xml.sax.SAXException;
//
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.ParserConfigurationException;
//import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerFactory;
//import javax.xml.transform.dom.DOMSource;
//import javax.xml.transform.stream.StreamResult;
//import java.io.File;
//import java.io.IOException;
//import java.util.Optional;
//
//public class CompilerMaven implements Compiler {
//
//    private final String SOURCE_DIR;
//
//    public CompilerMaven(String sourceDir) throws ParserConfigurationException, IOException, SAXException {
//        this.SOURCE_DIR = sourceDir;
//        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//        Document document = builder.parse(SOURCE_DIR + "/pom.xml");
//        NodeList plugins = document.getElementsByTagName("plugin");
//        for(int i = 0; i < plugins.getLength(); i++){
//            Optional<Node> conf = getChildByNameIfExists(plugins.item(i), "configuration");
//            if(conf.isPresent()){
//                Optional<Node> includes = getChildByNameIfExists(conf.get(), "includes");
//                if()
//            } else {
//                Node newConf = document.createElement("configuration");
//                Node newIncludes = document.createElement("includes");
//                Node newInclude = document.createElement("include");
//                newInclude.setNodeValue(sourceFile.getAbsolutePath());
//                newIncludes.appendChild(newInclude);
//                newConf.appendChild(newIncludes);
//                plugins.item(i).appendChild(newConf);
//            }
//        }
//        DOMSource dom = new DOMSource(document);
//        Transformer transformer = TransformerFactory.newInstance().newTransformer();
//        StreamResult result = new StreamResult(new File(SOURCE_DIR + "/tmp_pom.xml"));
//        transformer.transform(dom, result);
//    }
//
//    @Override
//    public File compile(File sourceFile) throws Exception {
//    }
//
//    private Optional<Node> getChildByNameIfExists(Node parent, String name){
//        NodeList child =  parent.getChildNodes();
//        for(int i = 0; i < child.getLength(); i++){
//            if (child.item(i).getNodeName().equals(name)) return Optional.of(child.item(i));
//        }
//        return Optional.empty();
//    }
//}
