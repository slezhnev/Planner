import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

/**
 * Класс для сохранения и чтения планов из файлов
 */
public class PlanUtils extends DefaultHandler {

    /**
     * Сохранение плана в файл. Перед сохранением спрашивает - куда сохранять
     *
     * @return true - если план был сохранен. false - иначе
     */
    public static boolean savePlanToFile() {
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File("./plans"));
        fc.setDialogTitle("Сохранение плана");
        fc.setFileFilter(new FileNameExtensionFilter("Файлы планов", "plan"));
        File file;
        if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
            if ((getExt(file) == null) || (!getExt(file).equals("plan"))) {
                file = new File(file.getPath() + ".plan");
            }
        } else {
            return false;
        }
        //
        if (!savePlan(file)) {
            JOptionPane.showMessageDialog(null, "Ошибка сохранения плана", "Сохранение плана", JOptionPane.ERROR_MESSAGE);
            return false;
        } else {
            // Дополнительно сохраним имя файла, с которым работали
            Properties props = new Properties();
            props.put("workFileName", file.getPath());
            try {
                props.storeToXML(new FileOutputStream("work.file"), "Work file");
            } catch (IOException ignored) {
            }
            //
            JOptionPane.showMessageDialog(null, "План сохранен в файле " + file.getPath(), "Сохранение плана", JOptionPane.INFORMATION_MESSAGE);
            return true;
        }
    }

    /**
     * Сохранение плана в файл
     *
     * @param file Куда сохранять
     * @return true - если все сохранилось, false - иначе
     */
    private static boolean savePlan(File file) {
        DocumentBuilderFactory factory
                = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            return false;
        }
        DOMImplementation impl = builder.getDOMImplementation();

        Document doc = impl.createDocument(null, null, null);

        Element root = doc.createElement("plan");
        root.setAttribute("quarter", "" + (Starter.getMainForm().getSelectedQuarter()));
        root.setAttribute("year", Starter.getMainForm().getYear());
        doc.appendChild(root);

        // Сохраняем работников
        Element e1 = doc.createElement("workers");
        root.appendChild(e1);
        for (Worker worker : Starter.getMainForm().getWorkers()) {
            Element e2 = doc.createElement("worker");
            e2.setAttribute("name", worker.getName());
            e2.setAttribute("isOverhead", "" + worker.isOverhead());
            e2.setAttribute("isNotInReport", "" + worker.isNotInMonthReport());
            e1.appendChild(e2);
        }

        // Поехали сохранять план
        e1 = doc.createElement("planParts");
        root.appendChild(e1);
        for (PlanPart part : Starter.getMainForm().getPlan()) {
            Element e2 = doc.createElement("planPart");
            e2.setAttribute("name", part.getName());
            e2.setAttribute("longName", part.getLongName());
            e1.appendChild(e2);
            for (WorkInPlan work : part.getWorks()) {
                Element e3 = doc.createElement("work");
                e3.setAttribute("name", work.getName());
                e3.setAttribute("endDate", work.getEndDate());
                e3.setAttribute("maked", "" + (work.isMaked() ? 1 : 0));
                // desc
                Element e4 = doc.createElement("desc");
                Text text = doc.createTextNode(work.getDesc());
                e4.appendChild(text);
                e3.appendChild(e4);
                // reserve
                e4 = doc.createElement("reserve");
                text = doc.createTextNode(work.getReserve());
                e4.appendChild(text);
                e3.appendChild(e4);
                // finishDocs
                e4 = doc.createElement("finishDocs");
                text = doc.createTextNode(work.getFinishDoc());
                e4.appendChild(text);
                e3.appendChild(e4);
                for (WorkerInPlan worker : work.getWorkersInPlan()) {
                    e4 = doc.createElement("worker");
                    e4.setAttribute("name", worker.getWorker().getName());
                    e4.setAttribute("labor", "" + worker.getLaborContent());
                    for (int i = 0; i < 3; i++) e4.setAttribute("perMonth" + (i + 1), "" + worker.getPerMonth()[i]);
                    e3.appendChild(e4);
                }
                //
                e2.appendChild(e3);
            }
        }

        //
        // Сохраняем
        DOMSource domSource = new DOMSource(doc);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tf.newTransformer();
        } catch (TransformerConfigurationException e) {
            return false;
        }
        //transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        try {
            transformer.transform(domSource, new StreamResult(new FileWriter(file)));
        } catch (TransformerException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            return false;
        }
        //
        return true;
    }

    /**
     * Получение расширения файла
     *
     * @param f файл
     * @return расширение файла
     */
    private static String getExt(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');
        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }


    /**
     * Формирование квартального плана
     *
     * @throws java.io.IOException В случае ошибок ввода-вывода при формировании и сохранении XML
     * @throws javax.xml.parsers.ParserConfigurationException
     *                             см. @javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder()
     * @throws javax.xml.transform.TransformerException
     *                             см. @javax.xml.transform.Transformer
     */
    public static void makeQuarterPlan() throws IOException, TransformerException, ParserConfigurationException {
        //File file = File.createTempFile("quarter", "plan");
        File file = new File("quarterPlan.toReport");
        DocumentBuilderFactory factory
                = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation impl = builder.getDOMImplementation();

        Document doc = impl.createDocument(null, null, null);

        Element root = doc.createElement("plan");
        root.setAttribute("quarter", "" + (Starter.getMainForm().getSelectedQuarter()));
        root.setAttribute("year", Starter.getMainForm().getYear());
        doc.appendChild(root);

        // Поехали сохранять план
        Element e1 = doc.createElement("planParts");
        root.appendChild(e1);
        //
        Element works = doc.createElement("works");
        root.appendChild(works);
        int planPartId = 1;
        for (PlanPart part : Starter.getMainForm().getPlan()) {
            double total = 0;
            Element e2 = doc.createElement("planPart");
            e2.setAttribute("name", part.getName());
            e2.setAttribute("longName", part.getLongName());
            e2.setAttribute("id", "" + planPartId);
            int workId = 1;
            for (WorkInPlan work : part.getWorks()) {
                Element e3 = doc.createElement("work");
                e3.setAttribute("id", "" + workId);
                e3.setAttribute("planPartId", "" + planPartId);
                e3.setAttribute("name", work.getName());
                e3.setAttribute("endDate", work.getEndDate());
                // desc
                Element e4 = doc.createElement("desc");
                Text text = doc.createTextNode(work.getDesc());
                e4.appendChild(text);
                e3.appendChild(e4);
                // reserve
                e4 = doc.createElement("reserve");
                text = doc.createTextNode(work.getReserve());
                e4.appendChild(text);
                e3.appendChild(e4);
                // finishDocs
                e4 = doc.createElement("finishDocs");
                text = doc.createTextNode(work.getFinishDoc());
                e4.appendChild(text);
                e3.appendChild(e4);
                // labor
                total = total + work.getLaborTotal();
                e4 = doc.createElement("labor");
                String sLabor;
                if (work.getLaborTotal() == 0) {
                    sLabor = "";
                } else {

                    sLabor = "" + (work.getLaborTotal() * 22) + " ч/д (" + work.getLaborTotal() + " ч/м)";
                }
                text = doc.createTextNode(sLabor);
                e4.appendChild(text);
                e3.appendChild(e4);
                //
                //e2.appendChild(e3);
                works.appendChild(e3);
                //
                workId++;
            }
            e2.setAttribute("labor", "" + total);
            //
            e1.appendChild(e2);
            //
            planPartId++;
        }

        //
        // Сохраняем
        DOMSource domSource = new DOMSource(doc);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        transformer = tf.newTransformer();
        //transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(domSource, new StreamResult(new FileWriter(file)));
    }


    /**
     * Загружает план из файла
     *
     * @return true - если план был сохранен. false - иначе
     */
    public static boolean loadPlanFromFile() {
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File("./plans"));
        fc.setDialogTitle("Загрузка плана");
        fc.setFileFilter(new FileNameExtensionFilter("Файлы планов", "plan"));
        File file;
        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
        } else {
            return false;
        }
        //
        if (!loadPlan(file)) {
            JOptionPane.showMessageDialog(null, "Ошибка загрузки плана", "загрузка плана", JOptionPane.ERROR_MESSAGE);
            return false;
        } else {
            JOptionPane.showMessageDialog(null, "План успешно загружен", "Загрузка плана", JOptionPane.INFORMATION_MESSAGE);
            return true;
        }
    }

    /**
     * Загружает план из файла
     *
     * @param file Откуда грузить план
     * @return true - если план загружен, false - иначе
     */
    public static boolean loadPlan(File file) {
        PlanUtils util = new PlanUtils();
        try {
            util.parse(file);
        } catch (SAXException e) {
            return false;
        } catch (ParserConfigurationException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private void parse(File file) throws SAXException, ParserConfigurationException, IOException {
        // Сбрасываем все...
        Starter.getMainForm().getWorkers().clear();
        Starter.getMainForm().getPlan().clear();
        // Грузим
        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser parser = spf.newSAXParser();
        parser.parse(new FileInputStream(file), this);
    }


    private StringBuffer tempVal;
    private PlanPart tempPlanPart = null;
    private WorkInPlan tempWork = null;
    private boolean inWorkers = false;
    private boolean inWork = false;

    /**
     * Receive notification of the start of an element.
     *
     * @param uri        The Namespace URI, or the empty string if the
     *                   element has no Namespace URI or if Namespace
     *                   processing is not being performed.
     * @param localName  The local name (without prefix), or the
     *                   empty string if Namespace processing is not being
     *                   performed.
     * @param qName      The qualified name (with prefix), or the
     *                   empty string if qualified names are not available.
     * @param attributes The attributes attached to the element.  If
     *                   there are no attributes, it shall be an empty
     *                   Attributes object.
     * @throws org.xml.sax.SAXException Any SAX exception, possibly
     *                                  wrapping another exception.
     * @see org.xml.sax.ContentHandler#startElement
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        tempVal = new StringBuffer("");
        if ("plan".equalsIgnoreCase(qName)) {
            try {
                Starter.getMainForm().setSelectedQuarter(Integer.parseInt(attributes.getValue("quarter")) - 1);
                Starter.getMainForm().setYear(attributes.getValue("year"));
            } catch (NumberFormatException e) {
                throw new SAXException("Ошибка чтения квартала");
            }
        } else if ("workers".equalsIgnoreCase(qName)) {
            inWorkers = true;
        } else if ("worker".equalsIgnoreCase(qName)) {
            // Если мы строим список работников - то будем добавлять его. Если нет - то будем искать...
            if (inWorkers) {
                Starter.getMainForm().getWorkers().add(new Worker(attributes.getValue("name"), 0.0,
                        Boolean.valueOf(attributes.getValue("isOverhead")),
                        Boolean.valueOf(attributes.getValue("isNotInReport"))));
            } else if (inWork) {
                if (tempWork != null) {
                    String name = attributes.getValue("name");
                    for (Worker worker : Starter.getMainForm().getWorkers()) {
                        if (worker.getName().equals(name)) {
                            try {
                                WorkerInPlan workerInPlan = new WorkerInPlan(worker, Double.parseDouble(attributes.getValue("labor")));
                                workerInPlan.getPerMonth()[0] = Double.parseDouble(attributes.getValue("perMonth1"));
                                workerInPlan.getPerMonth()[1] = Double.parseDouble(attributes.getValue("perMonth2"));
                                workerInPlan.getPerMonth()[2] = Double.parseDouble(attributes.getValue("perMonth3"));
                                tempWork.getWorkersInPlan().add(workerInPlan);
                            } catch (NumberFormatException e) {
                                throw new SAXException("Ошибка чтения работника в работе");
                            }
                            break;
                        }
                    }
                }
            }
        } else if ("planPart".equalsIgnoreCase(qName)) {
            tempPlanPart = new PlanPart(attributes.getValue("name"), attributes.getValue("longName"));
        } else if ("work".equalsIgnoreCase(qName)) {
            tempWork = new WorkInPlan(attributes.getValue("name"), "");
            tempWork.setEndDate(attributes.getValue("endDate"));
            if ((attributes.getValue("maked") == null)||(!attributes.getValue("maked").equals("1")))  tempWork.setMaked(false);
            else tempWork.setMaked(true);
            inWork = true;
        }
    }

    /**
     * Receive notification of the end of an element.
     *
     * @param uri       The Namespace URI, or the empty string if the
     *                  element has no Namespace URI or if Namespace
     *                  processing is not being performed.
     * @param localName The local name (without prefix), or the
     *                  empty string if Namespace processing is not being
     *                  performed.
     * @param qName     The qualified name (with prefix), or the
     *                  empty string if qualified names are not available.
     * @throws org.xml.sax.SAXException Any SAX exception, possibly
     *                                  wrapping another exception.
     * @see org.xml.sax.ContentHandler#endElement
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if ("workers".equalsIgnoreCase(qName)) {
            inWorkers = false;
        } else if ("planPart".equalsIgnoreCase(qName)) {
            Starter.getMainForm().getPlan().add(tempPlanPart);
            tempPlanPart = null;
        } else if ("work".equalsIgnoreCase(qName)) {
            if (tempPlanPart != null) {
                tempPlanPart.getWorks().add(tempWork);
            }
            tempWork = null;
            inWork = false;
        } else if ("desc".equalsIgnoreCase(qName)) {
            if (tempWork != null) tempWork.setDesc(tempVal.toString());
        } else if ("reserve".equalsIgnoreCase(qName)) {
            if (tempWork != null) tempWork.setReserve(tempVal.toString());
        } else if ("finishDocs".equalsIgnoreCase(qName)) {
            if (tempWork != null) tempWork.setFinishDoc(tempVal.toString());
        }
    }

    /**
     * Receive notification of character data inside an element.
     *
     * @param ch     The characters.
     * @param start  The start position in the character array.
     * @param length The number of characters to use from the
     *               character array.
     * @throws org.xml.sax.SAXException Any SAX exception, possibly
     *                                  wrapping another exception.
     * @see org.xml.sax.ContentHandler#characters
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        tempVal.append(new String(ch, start, length));
    }

    /**
     * Формирует XML для печати месячного отчета
     *
     * @param month     номер месяца в квартале
     * @param monthName название месяца
     * @throws java.io.IOException В случае ошибок ввода-вывода при формировании и сохранении XML
     * @throws javax.xml.parsers.ParserConfigurationException
     *                             см. @javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder()
     * @throws javax.xml.transform.TransformerException
     *                             см. @javax.xml.transform.Transformer
     */
    public static void makeMonthPlan(int month, String monthName) throws ParserConfigurationException, TransformerException, IOException {
        File file = new File("monthReport.toReport");
        DocumentBuilderFactory factory
                = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation impl = builder.getDOMImplementation();

        Document doc = impl.createDocument(null, null, null);

        Element root = doc.createElement("monthReport");
        root.setAttribute("month", monthName);
        root.setAttribute("year", Starter.getMainForm().getYear());
        doc.appendChild(root);

        // Поехали сохранять план
        Element e1 = doc.createElement("workers");
        root.appendChild(e1);
        for (Worker worker : Starter.getMainForm().getWorkers()) {
            if (!worker.isNotInMonthReport()) {
                Element e2 = doc.createElement("worker");
                e1.appendChild(e2);
                e2.setAttribute("name", worker.getName());
                Element e3 = doc.createElement("works");
                Text text = null;
                if (worker.isOverhead()) {
                    e2.setAttribute("labor", "Накладные расходы");
                    text = doc.createTextNode("-");
                    e3.appendChild(text);
                } else {
                    ArrayList<String> works = new ArrayList<String>();
                    double totalLaborInMonth = 0;
                    for (int i = 0; i < Starter.getMainForm().getPlan().size(); i++) {
                        for (WorkInPlan work : Starter.getMainForm().getPlan().get(i).getWorks()) {
                            for (WorkerInPlan workerInPlan : work.getWorkersInPlan()) {
                                if (worker.getName().equals(workerInPlan.getWorker().getName())) {
                                    // Значит это тот работник
                                    totalLaborInMonth = totalLaborInMonth + workerInPlan.getPerMonth()[month];
                                    if (workerInPlan.getPerMonth()[month] > 0) {
                                        if (works.indexOf(work.getName()) == -1) {
                                            works.add(work.getName());
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (totalLaborInMonth == 0) {
                        e2.setAttribute("labor", "-");
                        text = doc.createTextNode("-");
                    } else {
                        e2.setAttribute("labor", "" + totalLaborInMonth);
                        StringBuffer str = new StringBuffer();
                        for (String work : works) {
                            if (str.length() != 0) str.append("\n");
                            str.append(work);
                        }
                        text = doc.createTextNode(str.toString());
                    }
                    if (text != null) e3.appendChild(text);
                }
                e2.appendChild(e3);
            }
        }
        e1 = doc.createElement("works");
        root.appendChild(e1);
        HashMap<String, Double> works = new HashMap<String, Double>();
        for (PlanPart planPart : Starter.getMainForm().getPlan()) {
            for (WorkInPlan work : planPart.getWorks()) {
                double totalLaborInMonth = 0;
                for (WorkerInPlan worker : work.getWorkersInPlan()) {
                    totalLaborInMonth = totalLaborInMonth + worker.getPerMonth()[month];
                }
                if (works.containsKey(work.getName())) {
                    works.put(work.getName(), works.get(work.getName()) + totalLaborInMonth);
                } else {
                    works.put(work.getName(), totalLaborInMonth);
                }
            }
        }
        for (String key : works.keySet()) {
            if (works.get(key) > 0) {
                Element e2 = doc.createElement("work");
                e2.setAttribute("name", key);
                if (works.get(key) == 0) {
                    e2.setAttribute("labor", "-");
                } else {
                    e2.setAttribute("labor", "" + works.get(key));
                }
                e1.appendChild(e2);
            }
        }


        //
        // Сохраняем
        DOMSource domSource = new DOMSource(doc);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        transformer = tf.newTransformer();
        //transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(domSource, new StreamResult(new FileWriter(file)));
    }


    /**
     * Формирование плана по людям
     *
     * @throws java.io.IOException В случае ошибок ввода-вывода при формировании и сохранении XML
     * @throws javax.xml.parsers.ParserConfigurationException
     *                             см. @javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder()
     * @throws javax.xml.transform.TransformerException
     *                             см. @javax.xml.transform.Transformer
     */
    public static void makePerWorkerPlan() throws IOException, TransformerException, ParserConfigurationException {
        File file = new File("perWorkerPlan.toReport");
        DocumentBuilderFactory factory
                = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation impl = builder.getDOMImplementation();

        Document doc = impl.createDocument(null, null, null);

        Element root = doc.createElement("perWorkerPlan");
        root.setAttribute("quarter", "" + (Starter.getMainForm().getSelectedQuarter()));
        root.setAttribute("year", Starter.getMainForm().getYear());
        doc.appendChild(root);

        // Поехали сохранять план
        Element e1 = doc.createElement("workers");
        root.appendChild(e1);
        int workerId = 1;
        for (Worker worker : Starter.getMainForm().getWorkers()) {
            Element e2 = doc.createElement("worker");
            e1.appendChild(e2);
            e2.setAttribute("name", worker.getName());
            e2.setAttribute("id", "" + workerId);
            Text text = null;
            if (worker.isOverhead()) {
                e2.setAttribute("labor", "Накладные расходы");
                text = doc.createTextNode("-");
                e2.appendChild(text);
            } else {
                HashMap<String, Double> works = new HashMap<String, Double>();
                double totalLabor = 0;
                for (int i = 0; i < Starter.getMainForm().getPlan().size(); i++) {
                    for (WorkInPlan work : Starter.getMainForm().getPlan().get(i).getWorks()) {
                        for (WorkerInPlan workerInPlan : work.getWorkersInPlan()) {
                            if (worker.getName().equals(workerInPlan.getWorker().getName())) {
                                // Значит это тот работник
                                totalLabor = totalLabor + workerInPlan.getLaborContent();
                                if (workerInPlan.getLaborContent() > 0) {
                                    if (works.containsKey(work.getName())) {
                                        works.put(work.getName(), works.get(work.getName()) + workerInPlan.getLaborContent());
                                    } else {
                                        works.put(work.getName(), workerInPlan.getLaborContent());
                                    }
                                }
                            }
                        }
                    }
                }
                if (totalLabor == 0) {
                    e2.setAttribute("labor", "-");
                    Element e4 = doc.createElement("work");
                    e4.setAttribute("workerId", "" + workerId);
                    e4.setAttribute("name", "-");
                    e4.setAttribute("labor", "-");
                    e2.appendChild(e4);
                } else {
                    e2.setAttribute("labor", "" + totalLabor);
                    for (String work : works.keySet()) {
                        Element e4 = doc.createElement("work");
                        e4.setAttribute("workerId", "" + workerId);
                        e4.setAttribute("name", work);
                        e4.setAttribute("labor", "" + works.get(work));
                        e2.appendChild(e4);
                    }
                }
            }
            workerId++;
        }
        e1 = doc.createElement("works");
        root.appendChild(e1);
        HashMap<String, Double> works = new HashMap<String, Double>();
        for (PlanPart planPart : Starter.getMainForm().getPlan()) {
            for (WorkInPlan work : planPart.getWorks()) {
                double totalLabor = 0;
                for (WorkerInPlan worker : work.getWorkersInPlan()) {
                    totalLabor = totalLabor + worker.getLaborContent();
                }
                if (works.containsKey(work.getName())) {
                    works.put(work.getName(), works.get(work.getName()) + totalLabor);
                } else {
                    works.put(work.getName(), totalLabor);
                }
            }
        }
        for (String key : works.keySet()) {
            if (works.get(key) > 0) {
                Element e2 = doc.createElement("work");
                e2.setAttribute("name", key);
                if (works.get(key) == 0) {
                    e2.setAttribute("labor", "-");
                } else {
                    e2.setAttribute("labor", "" + works.get(key));
                }
                e1.appendChild(e2);
            }
        }


        //
        // Сохраняем
        DOMSource domSource = new DOMSource(doc);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        transformer = tf.newTransformer();
        //transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(domSource, new StreamResult(new FileWriter(file)));
    }

}
