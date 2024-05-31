package com.example.mybatplusdemo.controller;

import com.example.mybatplusdemo.entity.CreateContractInfo;
import com.example.mybatplusdemo.entity.FreeBillDetailVo;
import com.example.mybatplusdemo.entity.FreeContractClauseDto;
import com.example.mybatplusdemo.utils.PdfUtils;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@RestController("")
@RequestMapping("/pdf")
public class PDFController {

//    @GetMapping(value = "/create")
    @GetMapping("/create")
    public void create(@ApiParam(value = "0:销售合同,1:第三方合同") @RequestParam(name = "type") String type,
                       @RequestParam(name = "contractId") String contractId,
                       HttpServletResponse response) throws IOException {
        File pdfFile = null;
        if ("0".equals(type)) {
            /* 合同条款 */

//            //到业务方法 xx代替，这里主要是根据条件查询从表信息
//            List<xxx> 1 =getXXX();
//            //设计到业务方法 xx代替，这里主要是根据条件查询从表信息
//            List<xxx> 1 =getXXX();
//            // 仪表经验计费条款
//            List<xxx> 1 =getXXX();
//
//            /* 其它费用 */
//            List<xxx> 1 =getXXX();
            //导出方法
//            pdfFile = contractPdfService.createContractInfo(createContractInfo);
            CreateContractInfo createContractInfo = new CreateContractInfo();
            createContractInfo.setContractName("test");
            List<FreeContractClauseDto> freeContractClauseDtoArrayList = new ArrayList<>();
            FreeContractClauseDto freeContractClauseDto = new FreeContractClauseDto();
            freeContractClauseDto.setChargeStandard("test");
            freeContractClauseDto.setFreeRemarks("test");
            freeContractClauseDto.setHouseName("test");
            freeContractClauseDto.setMeterNo("test");
            freeContractClauseDto.setSetName("test");
            freeContractClauseDtoArrayList.add(freeContractClauseDto);
            createContractInfo.setContractClauseDtos(freeContractClauseDtoArrayList);
            createContractInfo.setDeviceContractClauseDtos(freeContractClauseDtoArrayList);
            createContractInfo.setElseFreeBillDetailVos(new ArrayList<>());
            createContractInfo.setFreeBillDetailVos(new ArrayList<>());
            pdfFile = createContractInfo(createContractInfo);
        }
        response.setContentType("application/pdf");
        if (pdfFile.exists()) {
            FileInputStream in = new FileInputStream(pdfFile);
            OutputStream out = response.getOutputStream();
            byte[] b = new byte[1024 * 5];
            int n;
            while ((n = in.read(b)) != -1) {
                out.write(b, 0, n);
            }
            out.flush();
            in.close();
            out.close();
        }
    }

    public File createContractInfo(CreateContractInfo contractInfo){
        try {
            // 1.新建document对象 建立一个Document对象
            Document document = new Document(PageSize.A4);

            PdfUtils.htFile.createNewFile();
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(PdfUtils.htFile));

            // 3.打开文档
            document.open();
            document.addTitle(contractInfo.getContractName());// 标题
            document.addAuthor("chuz");// 作者
            document.addSubject("Subject@iText pdf sample");// 主题
            document.addKeywords("Keywords@iTextpdf");// 关键字
            document.addCreator("chuz develop");// 创建者

            // 4.向文档中添加内容
            generatePDF(document,contractInfo);
            // 5.关闭文档
            document.close();
            writer.close();

            return PdfUtils.htFile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return PdfUtils.htFile;
    }

    // 生成PDF文件
    public void generatePDF(Document document,CreateContractInfo contractInfo) throws Exception {
        //添加总标题,
        PdfUtils.addTitle(document,contractInfo.getContractName(),1);
        // 基本信息-一级标题
        PdfUtils.addFirstTitle(document,"(一)基本信息",0);

        PdfPTable table = PdfUtils.createTable();
        PdfUtils.addNoBorderCell(table, StringUtils.join("合同编号：",contractInfo.getContractNo()));
        PdfUtils.addNoBorderCell(table,StringUtils.join("出租方：",contractInfo.getFirstPartyName()));
        PdfUtils.addNoBorderCell(table,StringUtils.join("出租方：",contractInfo.getFirstPartyName()));
        PdfUtils.addNoBorderCell(table,StringUtils.join("租赁方：",contractInfo.getPartyBName()));
        PdfUtils.addNoBorderCell(table,StringUtils.join("经办人：",contractInfo.getHandledByName()));
//        PdfUtils.addNoBorderCell(table,StringUtils.join("签订日期：", DateFormatUtil.dateToString(DateFormatUtil.yyyy_MM_dd,contractInfo.getSigningTime())));
//        PdfUtils.addNoBorderCell(table,StringUtils.join("业态：",contractInfo.getContractTypeName()));
//        PdfUtils.addNoBorderCell(table,StringUtils.join("意向来源：",dictTrans("customer_source",contractInfo.getContractSource())));
//        PdfUtils.addNoBorderCell(table,StringUtils.join("合同标签：",contractInfo.getContractLabel()));
//        PdfUtils.addNoBorderCell(table,StringUtils.join("租赁日期：",StringUtils.join(
//                DateFormatUtil.dateToString(DateFormatUtil.yyyy_MM_dd,contractInfo.getStartTime()),
//                "~",
//                DateFormatUtil.dateToString(DateFormatUtil.yyyy_MM_dd,contractInfo.getEndTime()))));
        document.add(table);
        PdfUtils.addLine(document);


        // 合同条款-一级标题
        PdfUtils.addFirstTitle(document,"(二)合同条款",0);

        PdfUtils.addSecondTitle(document,"合同计费条款",PdfUtils.titleSecondFont,0);
        PdfPTable itemTable = PdfUtils.createTitleTable(5,PdfUtils.tableCellFont,"资源","收费项","计费方式","计费周期","计算方式");
        for (FreeContractClauseDto contractClauseDto : contractInfo.getContractClauseDtos()) {
            PdfUtils.addCell(itemTable,contractClauseDto.getHouseName(),1,PdfUtils.tableCellFont);
            PdfUtils.addCell(itemTable,contractClauseDto.getSetName(),1,PdfUtils.tableCellFont);
//            PdfUtils.addCell(itemTable,dictTrans("free_calculate_way",contractClauseDto.getCalculateWay()),1,PdfUtils.tableCellFont);
//            PdfUtils.addCell(itemTable,dictTrans("charging_cycle",contractClauseDto.getChargingCycle()),1,PdfUtils.tableCellFont);
            PdfUtils.addCell(itemTable,contractClauseDto.getFreeRemarks(),1,PdfUtils.tableCellFont);
        }
        document.add(itemTable);

        PdfUtils.addSecondTitle(document,"仪表/经营计费条款",PdfUtils.titleSecondFont,0);
        PdfPTable deviceTable = PdfUtils.createTitleTable(4,PdfUtils.tableCellFont,"资源","收费项","计费方式","仪表/录数项");
        for (FreeContractClauseDto deviceContractClauseDto : contractInfo.getDeviceContractClauseDtos()) {
            PdfUtils.addCell(deviceTable,deviceContractClauseDto.getHouseName(),1,PdfUtils.tableCellFont);
            PdfUtils.addCell(deviceTable,deviceContractClauseDto.getSetName(),1,PdfUtils.tableCellFont);
            PdfUtils.addCell(deviceTable,deviceContractClauseDto.getChargeStandard(),1,PdfUtils.tableCellFont);
            PdfUtils.addCell(deviceTable,deviceContractClauseDto.getMeterNo(),1,PdfUtils.tableCellFont);
        }
        document.add(deviceTable);

        PdfUtils.addSecondTitle(document,"其他费用添加",PdfUtils.titleSecondFont,0);
        PdfPTable elseFreeTable = PdfUtils.createTitleTable(8,PdfUtils.tableCellFont,"资源","收费项","应收(元)","实收(元)","欠收(元)","应收日期","账期","备注");
        for (FreeBillDetailVo elseFreeBillDetailVo : contractInfo.getElseFreeBillDetailVos()) {
            PdfUtils.addCell(elseFreeTable,elseFreeBillDetailVo.getHouseName(),1,PdfUtils.tableCellFont);
            PdfUtils.addCell(elseFreeTable,"收费项",1,PdfUtils.tableCellFont);
            PdfUtils.addCell(elseFreeTable,elseFreeBillDetailVo.getOughtAmount(),1,PdfUtils.tableCellFont);
            PdfUtils.addCell(elseFreeTable,elseFreeBillDetailVo.getPracticalAmount(),1,PdfUtils.tableCellFont);
            PdfUtils.addCell(elseFreeTable,elseFreeBillDetailVo.getOweAmount(),1,PdfUtils.tableCellFont);
//            PdfUtils.addCell(elseFreeTable,DateFormatUtil.dateToString(DateFormatUtil.yyyy_MM_dd,elseFreeBillDetailVo.getOughtDate()),1,PdfUtils.tableCellFont);
            PdfUtils.addCell(elseFreeTable,elseFreeBillDetailVo.getPeriod(),1,PdfUtils.tableCellFont);
            PdfUtils.addCell(elseFreeTable,elseFreeBillDetailVo.getRemarks(),1,PdfUtils.tableCellFont);
        }
        document.add(elseFreeTable);


        PdfUtils.addLine(document);

        // 其它约定
        PdfUtils.addFirstTitle(document,"(三)其它约定",0);
        PdfPTable elseTable = PdfUtils.createTable();
        PdfUtils.addNoBorderCell(elseTable,"设施/服务：设施/服务");
        PdfUtils.addNoBorderCell(elseTable,"特殊约定：特殊约定");
        PdfUtils.addNoBorderCell(elseTable,"违约说明：违约说明");
        PdfUtils.addNoBorderCell(elseTable,"");
        document.add(elseTable);
        PdfUtils.addLine(document);

        // 合同账单-自定义
        Paragraph paragraph = new Paragraph("(四)合同账单", new Font(PdfUtils.bfChinese, 14, Font.NORMAL));
        paragraph.setAlignment(0); //设置文字居中 0靠左   1，居中     2，靠右
        paragraph.setIndentationLeft(0); //设置左缩进
        paragraph.setIndentationRight(0); //设置右缩进
        paragraph.setFirstLineIndent(0); //设置首行缩进
        paragraph.setLeading(7f); //行间距
        paragraph.setSpacingBefore(20f); //设置段落上空白
        paragraph.setSpacingAfter(25f); //设置段落下空白
        document.add(paragraph);

        PdfPTable billTable = PdfUtils.createTitleTable(10,PdfUtils.tableCellFont,"费用项目","费用类型","费用标识","账期","起止时间","应收金额(元)","实收金额(元)","欠费金额(元)","缴费时间","状态");
        List<FreeBillDetailVo> freeBillDetailVos = contractInfo.getFreeBillDetailVos();
        for (FreeBillDetailVo freeBillDetailVo : freeBillDetailVos) {
            PdfUtils.addCell(billTable,"租金",1,PdfUtils.tableCellFont);
            PdfUtils.addCell(billTable,"租金",1,PdfUtils.tableCellFont);
            PdfUtils.addCell(billTable,"周期性收费",1,PdfUtils.tableCellFont);
            PdfUtils.addCell(billTable,freeBillDetailVo.getPeriod(),1,PdfUtils.tableCellFont);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateTime=sdf.format(freeBillDetailVo.getBeginTime())+"~"+sdf.format(freeBillDetailVo.getEndTime());
            PdfUtils.addCell(billTable,dateTime,1,PdfUtils.tableCellFont);
            PdfUtils.addCell(billTable,freeBillDetailVo.getPracticalAmount(),1,PdfUtils.tableCellFont);
            PdfUtils.addCell(billTable,freeBillDetailVo.getOughtAmount(),1,PdfUtils.tableCellFont);
            PdfUtils.addCell(billTable,freeBillDetailVo.getOweAmount(),1,PdfUtils.tableCellFont);
//            PdfUtils.addCell(billTable,DateFormatUtil.dateToString(DateFormatUtil.yyyy_MM_dd_HHmmss,freeBillDetailVo.getPayTime()),1,PdfUtils.tableCellFont);
            PdfUtils.addCell(billTable,"未结清",1,PdfUtils.tableCellFont);
        }
        document.add(billTable);
    }

}
