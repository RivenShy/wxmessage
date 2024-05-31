package com.example.mybatplusdemo.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.apache.commons.lang3.StringUtils;
import java.io.*;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class PdfUtils {

    /**
     * 字体对象
     */
    public static BaseFont bfChinese;

    /**
     * 定义全局的字体静态变量
     */
    public static Font titlefont;
    public static Font titleSecondFont;
    public static Font headfont;
    public static Font secondHeadfont;
    public static Font textfont;
    public static Font secondTitleFont;
    public static Font itemFont;
    public static Font paragraphFont;
    public static Font tableCellFont;
    public static Font accessoryFont;

    /**
     * 间距
     */
    public static String shortSpacing = "      ";
    public static String mediumSpacing = "            ";
    public static String longSpacing = "                        ";
    public static String maxLongSpacing = "                                    ";
    public static String shortSlash = "   /   ";
    public static String mediumSlash = "      /      ";
    public static String longSlash = "            /            ";
    public static String maxLongSlash = "                  /                  ";

    /**
     * 模版
     */
    public static String ht;
    public static String scan;
    public static String htScan;
    public static String over;
    public static String htSign;
    public static String signatureHt;
    public static File htFile;
    public static File overFile;
    public static File htSignFile;

    /**
     * 初始化字体及勾选、未勾选图标
     */
    static {
        try {
            // 不同字体（这里定义为同一种字体：包含不同字号、不同style）
            bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
            titlefont = new Font(bfChinese, 16, Font.NORMAL);
            titleSecondFont = new Font(bfChinese, 14, Font.NORMAL);
            headfont = new Font(bfChinese, 14, Font.BOLD);
            secondHeadfont = new Font(bfChinese, 15, Font.BOLD);
            textfont = new Font(bfChinese, 16, Font.NORMAL);
            secondTitleFont = new Font(bfChinese,18,Font.BOLD);
            itemFont = new Font(bfChinese,16,Font.BOLD);
            paragraphFont = new Font(bfChinese,11,Font.NORMAL);
            accessoryFont = new Font(bfChinese,13,Font.NORMAL);
            tableCellFont = new Font(bfChinese,9,Font.NORMAL);
            /**
             * 判断当前环境
             */
            String osName = System.getProperty("os.name").toLowerCase();
            if (osName.contains("win")||osName.contains("mac")) {
                ht = getResourceBasePath() + "\\templates\\ht.pdf";
                htFile = new File(ht);
            } else { //todo: linux或unbunt
                ht = "/mnt/jar/spring-cloud/ht.pdf";
                htFile = new File(ht);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取项目根路径
     *
     * @return
     */
    public static String getResourceBasePath() {
        // 获取根目录
        File path = null;
        try {
            path = new File(ResourceUtils.getURL("classpath:").getPath());
        } catch (FileNotFoundException e) {

        }

        if (path == null || !path.exists()) {
            path = new File("");
        }

        //todo: 就获取第三步中模版所在的位置，请看上列中 “3.模版加载里的内容”
        File file = new File(path.getAbsolutePath() + "\\templates");
        if (!file.exists()){
            return path.getAbsolutePath().replace("xx","xx");
        }
        return path.getAbsolutePath();
    }

    public static PdfPTable createTable() throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        // 设置总宽度
        table.setTotalWidth(490);
        table.setLockedWidth(true);
        // 设置每一列宽度
        table.setTotalWidth(new float[] {240,240});
        table.setLockedWidth(true);
        return table;
    }
    public PdfPTable createTable(int numColumns) {
        PdfPTable table = new PdfPTable(numColumns);
        // 设置总宽度
        table.setTotalWidth(490);
        table.setLockedWidth(true);
        return table;
    }

    public static PdfPTable createTitleTable(int numColumns,Font font, String ... titles) {
        PdfPTable table = new PdfPTable(numColumns);
        // 设置总宽度
        table.setTotalWidth(490);
        table.setLockedWidth(true);

        for (String title : titles) {
            addTitleCell(table,title,1,font);
        }
        return table;
    }

    /**
     * 添加居中 title
     * @param document
     * @param text
     * @throws DocumentException
     * todo: 字体大小16px
     */
    public static void addTitle(Document document, String text, int alignment) throws DocumentException {
        Paragraph paragraph = new Paragraph(text, new Font(bfChinese, 16, Font.NORMAL));
        paragraph.setAlignment(alignment); //设置文字居中 0靠左   1，居中     2，靠右
        paragraph.setIndentationLeft(0); //设置左缩进
        paragraph.setIndentationRight(0); //设置右缩进
        paragraph.setFirstLineIndent(0); //设置首行缩进
        paragraph.setLeading(10f); //行间距
        paragraph.setSpacingBefore(10f); //设置段落上空白
        paragraph.setSpacingAfter(10f); //设置段落下空白
        document.add(paragraph);
    }

    /**
     * 添加一级标题
     * @param document
     * @param text
     * @throws DocumentException
     */
    public static void addFirstTitle(Document document,String text,int alignment) throws DocumentException {
        Paragraph paragraph = new Paragraph(text, new Font(bfChinese, 14, Font.NORMAL));
        paragraph.setAlignment(alignment); //设置文字居中 0靠左   1，居中     2，靠右
        paragraph.setIndentationLeft(0); //设置左缩进
        paragraph.setIndentationRight(0); //设置右缩进
        paragraph.setFirstLineIndent(0); //设置首行缩进
        paragraph.setLeading(7f); //行间距
        paragraph.setSpacingBefore(20f); //设置段落上空白
        paragraph.setSpacingAfter(7f); //设置段落下空白
        document.add(paragraph);
    }

    /**
     * 添加二级标题
     * @param document
     * @param text
     * @throws DocumentException
     */
    public static void addSecondTitle(Document document,String text,Font font,int alignment) throws DocumentException {
        Paragraph paragraph = new Paragraph(text, new Font(bfChinese, 12, Font.NORMAL));
        paragraph.setAlignment(alignment); //设置文字居中 0靠左   1，居中     2，靠右
        paragraph.setIndentationLeft(9); //设置左缩进
        paragraph.setIndentationRight(0); //设置右缩进
        paragraph.setFirstLineIndent(9); //设置首行缩进
        paragraph.setLeading(15f); //行间距
        paragraph.setSpacingBefore(10f); //设置段落上空白
        paragraph.setSpacingAfter(5f); //设置段落下空白
        document.add(paragraph);
    }

    /**
     * 添加分割线
     * @param document
     * @throws DocumentException
     */
    public static void addLine(Document document) throws DocumentException {
        LineSeparator ls = new LineSeparator();
        ls.setLineWidth(1);
        ls.setLineColor(new BaseColor(179,180,164));
        Chunk chunk = new Chunk(ls);
        document.add(chunk);
    }

    public static void addNoBorderCell(PdfPTable table,String text) {
        PdfPCell cell = new PdfPCell(new Paragraph(text,paragraphFont));
        cell.setBorderWidth(0);
        cell.setLeading(24,0); //行间距
        table.addCell(cell);
    }

    public static void addTitleCell(PdfPTable table,String text,float borderWidth,Font font) {
        Paragraph paragraph = new Paragraph(text, font);
        paragraph.setAlignment(1);
        paragraph.setLeading(5f);
        PdfPCell cell = new PdfPCell(paragraph);
        cell.setBorderWidth(borderWidth);
        cell.setBackgroundColor(new BaseColor(245,247,251));
        cell.setHorizontalAlignment(1);
        cell.setVerticalAlignment(1);
        table.addCell(cell);
    }

    public static void addCell(PdfPTable table,String text,float borderWidth,Font font) {
        if (StringUtils.isBlank(text)) {
            text = " ";
        }
        Paragraph paragraph = new Paragraph(text, font);
        paragraph.setAlignment(1);
        PdfPCell cell = new PdfPCell(paragraph);
        //水平居中和垂直居中
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setUseAscender(true);
        table.addCell(cell);
    }

    public static void addCell(PdfPTable table, BigDecimal amount, float borderWidth, Font font) {
        Paragraph paragraph = null;
        if (Objects.isNull(amount)) {
            paragraph = new Paragraph(" ",font);
        } else {
            paragraph = new Paragraph(amount.toString(),font);
        }

        paragraph.setAlignment(1);
        PdfPCell cell = new PdfPCell(paragraph);
        cell.setBorderWidth(borderWidth);
        //水平居中和垂直居中
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setUseAscender(true);
        table.addCell(cell);
    }

    /**
     * 添加二级标题
     * @param document
     * @param text
     * @throws DocumentException
     */
    public void addSecondTitle(Document document,String text,int alignment,float spacingBefore,float spacingAfter) throws DocumentException {
        Paragraph paragraph10 = new Paragraph(text, secondTitleFont);
        paragraph10.setAlignment(alignment); //设置文字居中 0靠左   1，居中     2，靠右
        paragraph10.setIndentationLeft(12); //设置左缩进
        paragraph10.setIndentationRight(12); //设置右缩进
        paragraph10.setFirstLineIndent(32); //设置首行缩进
        paragraph10.setLeading(30f); //行间距
        paragraph10.setSpacingBefore(1f); //设置段落上空白
        paragraph10.setSpacingAfter(15f); //设置段落下空白
        document.add(paragraph10);
    }

    /**
     * 添加子项标题
     * @param document
     * @param text
     * @throws DocumentException
     */
    public void addItemTitle(Document document,String text) throws DocumentException {
        Paragraph paragraph = new Paragraph(text, itemFont);
        paragraph.setAlignment(0); //设置文字居中 0靠左   1，居中     2，靠右
        paragraph.setIndentationLeft(12); //设置左缩进
        paragraph.setIndentationRight(12); //设置右缩进
        paragraph.setFirstLineIndent(32); //设置首行缩进
        paragraph.setLeading(30f); //行间距
        paragraph.setSpacingBefore(1f); //设置段落上空白
        paragraph.setSpacingAfter(1f); //设置段落下空白
        document.add(paragraph);
    }

    /**
     * 添加段落 自动换行
     */
    public void addTextParagraph(Document document,String text) throws DocumentException {
        Paragraph paragraph = new Paragraph(text,paragraphFont);
        paragraph.setAlignment(0); //设置文字居中 0靠左   1，居中     2，靠右
        paragraph.setIndentationLeft(12); //设置左缩进
        paragraph.setIndentationRight(12); //设置右缩进
        paragraph.setFirstLineIndent(32); //设置首行缩进
        paragraph.setLeading(30f); //行间距
        document.add(paragraph);
    }

    /**
     * 添加段落 自动换行
     */
    public Paragraph createTextParagraph() throws DocumentException {
        Paragraph paragraph = new Paragraph("",paragraphFont);
        paragraph.setAlignment(0); //设置文字居中 0靠左   1，居中     2，靠右
        paragraph.setIndentationLeft(12); //设置左缩进
        paragraph.setIndentationRight(12); //设置右缩进
        paragraph.setFirstLineIndent(32); //设置首行缩进
        paragraph.setLeading(30f); //行间距
        return paragraph;
    }

    /**
     * 添加段落 自动换行
     */
    public Paragraph createTextParagraph(String text) throws DocumentException {
        Paragraph paragraph = new Paragraph(text,paragraphFont);
        paragraph.setAlignment(0); //设置文字居中 0靠左   1，居中     2，靠右
        paragraph.setIndentationLeft(12); //设置左缩进
        paragraph.setIndentationRight(12); //设置右缩进
        paragraph.setFirstLineIndent(32); //设置首行缩进
        paragraph.setLeading(30f); //行间距
        return paragraph;
    }

    /**
     * 创建段落 自动换行
     */
    public Paragraph createTextParagraph(String text,int alignment) {
        Paragraph paragraph = new Paragraph(text,paragraphFont);
        paragraph.setAlignment(alignment); //设置文字居中 0靠左   1，居中     2，靠右
        paragraph.setIndentationLeft(12); //设置左缩进
        paragraph.setIndentationRight(12); //设置右缩进
        paragraph.setFirstLineIndent(32); //设置首行缩进
        paragraph.setLeading(30f); //行间距
        return paragraph;
    }

    public void appendParagraph(Document document,Paragraph paragraph,String text) throws DocumentException {
        paragraph.add(text);
        document.add(paragraph);
    }

    /**
     * 添加下划线
     */
    public Paragraph addUnderLine(Paragraph paragraph,String text){
        if (StringUtils.isBlank(text)){
            text = shortSpacing;
        } else {
            text = StringUtils.join(" ",text," ");
        }
        Chunk sigUnderline = new Chunk(text);
        sigUnderline.setUnderline(0.1f, -2f);
        paragraph.add(sigUnderline);
        return paragraph;
    }

    /**
     * 添加下划线
     */
    public void addUnderLine(Document document,Paragraph paragraph,String text) throws DocumentException {
        if (StringUtils.isBlank(text)){
            text = shortSpacing;
        }
        Chunk sigUnderline = new Chunk(StringUtils.join(" ",text," "));
        sigUnderline.setUnderline(0.1f, -2f);
        paragraph.add(sigUnderline);
        document.add(paragraph);
    }

    /**objStmMark = null
     * 追加签名
     */
    public File mergePdf(String [] files,String savePath){
        PdfReader pdfReader = null;
        Document document = null;
        try {
            //创建一个与a.pdf相同纸张大小的document
            pdfReader = new PdfReader(files[0]);
            document = new Document(pdfReader.getPageSize(1));
            PdfCopy copy = new PdfCopy(document, new FileOutputStream(savePath));
            document.open();
            for (int i = 0; i < files.length; i++) {
                //一个一个的遍历现有的PDF
                PdfReader reader = new PdfReader(files[i]);
                int n = reader.getNumberOfPages();//PDF文件总共页数
                System.out.println("n:"+n);
                for (int j = 1; j <= n; j++) {
                    document.newPage();
                    PdfImportedPage page = copy.getImportedPage(reader, j);
                    copy.addPage(page);
                }
                reader.close();
            }
            document.close();
            pdfReader.close();
            return new File(savePath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } finally {
            document.close();
            pdfReader.close();
        }
        return null;
    }


    /**
     * 添加下划线
     */
    public Paragraph addUnderLine(Boolean check,Paragraph paragraph,String text1,String text2){
        Chunk sigUnderline = null;
        if (check){
            if (Objects.isNull(text1)){
                text1 = shortSpacing;
            } else {
                text1 = StringUtils.join(" ",text1," ");
            }
            sigUnderline = new Chunk(text1);
        } else {
            if (Objects.isNull(text2)){
                text2 = shortSpacing;
            } else {
                text2 = StringUtils.join(" ",text2," ");
            }
            sigUnderline = new Chunk(text2);
        }
        sigUnderline.setUnderline(0.1f, -2f);
        paragraph.add(sigUnderline);
        return paragraph;
    }

    /**
     * 添加下划线
     */
    public void addUnderLine(Document document,Boolean check,Paragraph paragraph,String text1,String text2) throws DocumentException {
        Chunk sigUnderline = null;
        if (check){
            if (Objects.isNull(text1)){
                text1 = shortSpacing;
            } else {
                text1 = StringUtils.join(" ",text1," ");
            }
            sigUnderline = new Chunk(text1);
        } else {
            if (Objects.isNull(text2)){
                text2 = shortSpacing;
            } else {
                text2 = StringUtils.join(" ",text2," ");
            }
            sigUnderline = new Chunk(text2);
        }

        sigUnderline.setUnderline(0.1F, -2.0F);
        paragraph.add(sigUnderline);
        document.add(paragraph);
    }

    /**
     * 添加下划线 Chunk
     */
    public Chunk createUnderLineChunk(String text) {
        if (Objects.isNull(text)){
            text = shortSpacing;
        } else {
            text = StringUtils.join(" ",text," ");
        }
        Chunk sigUnderline = new Chunk(text);
        sigUnderline.setUnderline(0.1f, -2f);
        return sigUnderline;
    }

    private Document createDocument() throws IOException {
        // 1.新建document对象 建立一个Document对象
        Document document = new Document(PageSize.A4);

        //
        htFile.createNewFile();

        // 3.打开文档
        document.open();
        document.addTitle("销售合同");// 标题
        document.addAuthor("chuz");// 作者
        document.addSubject("Subject@iText pdf sample");// 主题
        document.addKeywords("Keywords@iTextpdf");// 关键字
        document.addCreator("chuz develop");// 创建者
        return document;
    }

    /**
     * 添加下划线 Chunk
     */
    public Chunk createUnderLineChunk(Boolean check,String text1,String text2) throws DocumentException {
        Chunk sigUnderline = null;
        if (check){
            if (Objects.isNull(text1)){
                text1 = shortSpacing;
            } else {
                text1 = StringUtils.join(" ",text1," ");
            }
            sigUnderline = new Chunk(text1);
        } else {
            if (Objects.isNull(text2)){
                text2 = shortSpacing;
            } else {
                text2 = StringUtils.join(" ",text2," ");
            }
            sigUnderline = new Chunk(text2);
        }

        sigUnderline.setUnderline(0.1f, -2f);
        return sigUnderline;
    }

    /**
     * 添加下划线 Chunk
     */
    public Chunk createChunk(String text) throws DocumentException {
        if (StringUtils.isBlank(text)){
            text = shortSpacing;
        }
        text = StringUtils.join(" ",text," ");
        return new Chunk(text);
    }

    /**
     * 添加带选择框文本
     */
//    public void addCheck(Document document,Boolean check,String text) throws Exception {
//        Paragraph paragraph = createTextParagraph("");
//        if (check){
//            paragraph.add(new Chunk(checkPng,0,0,true));
//        } else {
//            paragraph.add(new Chunk(unCheckPng,0,0,true));
//        }
//        Chunk chunk = new Chunk(StringUtils.join(" ",text),textfont);
//        paragraph.add(chunk);
//        document.add(paragraph);
//    }

    /**
     * 添加带选择框文本
     */
//    public void addCheck(Paragraph paragraph,Boolean check,String text) throws Exception {
//        if (check){
//            paragraph.add(new Chunk(checkPng,0,0,true));
//        } else {
//            paragraph.add(new Chunk(unCheckPng,0,0,true));
//        }
//        Chunk chunk = new Chunk(StringUtils.join(" ",text),textfont);
//        paragraph.add(chunk);
//    }

    /**
     * 添加带选择框文本
     */
//    public Paragraph addCheck(Boolean check,String text) throws Exception {
//        Paragraph paragraph = createTextParagraph("");
//        if (check){
//            paragraph.add(new Chunk(checkPng,0,0,true));
//        } else {
//            paragraph.add(new Chunk(unCheckPng,0,0,true));
//        }
//        Chunk chunk = new Chunk(StringUtils.join(" ",text),textfont);
//        paragraph.add(chunk);
//        return paragraph;
//    }

    /**
     * 添加带选择框文本
     */
//    public Paragraph appendCheck(Paragraph paragraph,Boolean check,String text) throws Exception {
//        if (check){
//            paragraph.add(new Chunk(checkPng,0,0,true));
//        } else {
//            paragraph.add(new Chunk(unCheckPng,0,0,true));
//        }
//        Chunk chunk = new Chunk(StringUtils.join(" ",text),textfont);
//        paragraph.add(chunk);
//        return paragraph;
//    }

    public void packageDateParagraph(Document document, Paragraph paragraph, Date date) throws DocumentException {
        if (Objects.nonNull(date)){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            addUnderLine(paragraph,StringUtils.join(year));
            paragraph.add("年");
            addUnderLine(paragraph,StringUtils.join(month));
            paragraph.add("月");
            addUnderLine(paragraph,StringUtils.join(day));
            paragraph.add("日");
        } else {
            addUnderLine(paragraph,StringUtils.join(shortSlash));
            paragraph.add("年");
            addUnderLine(paragraph,StringUtils.join(shortSlash));
            paragraph.add("月");
            addUnderLine(paragraph,StringUtils.join(shortSlash));
            paragraph.add("日");
        }
        document.add(paragraph);
    }

    public MultipartFile fileToMultipartFile(File file) {

        FileItem fileItem = createFileItem(file);
        MultipartFile multipartFile = new CommonsMultipartFile(fileItem);
        return multipartFile;
    }

    public static FileItem createFileItem(File file) {
        FileItemFactory factory = new DiskFileItemFactory(16, null);
        FileItem item = factory.createItem("textField", "text/plain", true, file.getName());
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        try {
            FileInputStream fis = new FileInputStream(file);
            OutputStream os = item.getOutputStream();
            while ((bytesRead = fis.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return item;
    }

    public String precision(BigDecimal number){
        if (Objects.nonNull(number)){
            return number.setScale(0,BigDecimal.ROUND_DOWN).toString();
        }
        return null;
    }

}
