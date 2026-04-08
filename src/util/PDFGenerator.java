
package util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * PDFGenerator - Utilidad para generar reportes en PDF con iText.
 * Todos los reportes usan este generador.
 */
public class PDFGenerator {

    // ── Fuentes ──────────────────────────────────────────────────────────
    private static final Font FONT_TITLE  = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD,   BaseColor.DARK_GRAY);
    private static final Font FONT_SUB    = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.GRAY);
    private static final Font FONT_HEADER = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD,   BaseColor.WHITE);
    private static final Font FONT_CELL   = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, BaseColor.DARK_GRAY);

    private static final BaseColor COLOR_HEADER = new BaseColor(0x18, 0x5F, 0xA5);  // azul
    private static final BaseColor COLOR_ROW_ALT = new BaseColor(0xF1, 0xEF, 0xE8); // gris claro

    /**
     * Genera un reporte PDF con tabla.
     *
     * @param rutaArchivo  Ruta donde se guardará el PDF
     * @param titulo       Título del reporte
     * @param subtitulo    Subtítulo o descripción
     * @param columnas     Nombres de las columnas
     * @param filas        Datos (cada String[] es una fila)
     */
    public static void generar(String rutaArchivo, String titulo, String subtitulo,
                               String[] columnas, List<String[]> filas) throws DocumentException, IOException {

        Document doc = new Document(PageSize.A4.rotate(), 30, 30, 40, 40);
        PdfWriter.getInstance(doc, new FileOutputStream(rutaArchivo));
        doc.open();

        // ── Encabezado ──
        Paragraph pTitulo = new Paragraph(titulo, FONT_TITLE);
        pTitulo.setAlignment(Element.ALIGN_CENTER);
        pTitulo.setSpacingAfter(4);
        doc.add(pTitulo);

        Paragraph pSub = new Paragraph(subtitulo, FONT_SUB);
        pSub.setAlignment(Element.ALIGN_CENTER);
        pSub.setSpacingAfter(16);
        doc.add(pSub);

        // ── Línea separadora ──
        doc.add(new Paragraph(" "));

        // ── Tabla ──
        PdfPTable tabla = new PdfPTable(columnas.length);
        tabla.setWidthPercentage(100);

        // Headers
        for (String col : columnas) {
            PdfPCell cell = new PdfPCell(new Phrase(col, FONT_HEADER));
            cell.setBackgroundColor(COLOR_HEADER);
            cell.setPadding(8);
            cell.setBorderWidth(0);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            tabla.addCell(cell);
        }

        // Filas
        for (int i = 0; i < filas.size(); i++) {
            String[] fila = filas.get(i);
            BaseColor bgColor = (i % 2 == 0) ? BaseColor.WHITE : COLOR_ROW_ALT;
            for (String celda : fila) {
                PdfPCell cell = new PdfPCell(new Phrase(celda != null ? celda : "—", FONT_CELL));
                cell.setBackgroundColor(bgColor);
                cell.setPadding(7);
                cell.setBorderWidth(0);
                cell.setBorderWidthBottom(0.5f);
                cell.setBorderColorBottom(new BaseColor(0xD3, 0xD1, 0xC7));
                tabla.addCell(cell);
            }
        }

        if (filas.isEmpty()) {
            PdfPCell cell = new PdfPCell(new Phrase("Sin resultados para mostrar.", FONT_CELL));
            cell.setColspan(columnas.length);
            cell.setPadding(10);
            cell.setBorderWidth(0);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(cell);
        }

        doc.add(tabla);

        // ── Pie de página ──
        Paragraph pie = new Paragraph(
                "Mundial FIFA 2026 — Generado: " + new java.util.Date(), FONT_SUB);
        pie.setAlignment(Element.ALIGN_RIGHT);
        pie.setSpacingBefore(16);
        doc.add(pie);

        doc.close();
    }
}