package com.ems.service;

import com.ems.model.Attendance;
import com.ems.model.Department;
import com.ems.model.Employee;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.IOException;
import java.text.Normalizer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Exports tables to CSV and PDF files.
 */
public class ExportService {
    public Path exportEmployeesToCsv(List<Employee> employees, Path target) throws IOException {
        return writeCsv(target, "Employee ID,Name,Designation,Salary,Department,Phone,Email,Status,Joining Date", employees.stream()
                .map(e -> row(e.getEmployeeId(), e.getEmployeeName(), e.getDesignation(), e.getSalary(), e.getDepartment(), e.getPhoneNumber(), e.getEmail(), e.getStatus(), e.getJoiningDate()))
                .toArray(String[]::new));
    }

    public Path exportDepartmentsToCsv(List<Department> departments, Path target) throws IOException {
        return writeCsv(target, "Department ID,Name,Head,Total Employees,Description", departments.stream()
                .map(d -> row(d.getDepartmentId(), d.getDepartmentName(), d.getHead(), d.getTotalEmployees(), d.getDescription()))
                .toArray(String[]::new));
    }

    public Path exportAttendanceToCsv(List<Attendance> attendance, Path target) throws IOException {
        return writeCsv(target, "Attendance ID,Employee ID,Date,Status,Remarks", attendance.stream()
                .map(a -> row(a.getAttendanceId(), a.getEmployeeId(), a.getDate(), a.getStatus(), a.getRemarks()))
                .toArray(String[]::new));
    }

    public Path exportTextToPdf(String title, List<String> lines, Path target) throws IOException {
        if (target.getParent() != null) {
            Files.createDirectories(target.getParent());
        }

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contentStream.newLineAtOffset(50, 730);
            contentStream.showText(sanitizePdfText(title));
            contentStream.setFont(PDType1Font.HELVETICA, 10);

            int y = 710;
            for (String line : lines) {
                String safeLine = sanitizePdfText(line == null ? "" : line);
                List<String> wrappedLines = wrapLine(safeLine, 95);
                for (String wrapped : wrappedLines) {
                    if (y < 60) {
                        contentStream.endText();
                        contentStream.close();

                        page = new PDPage(PDRectangle.LETTER);
                        document.addPage(page);
                        contentStream = new PDPageContentStream(document, page);
                        contentStream.beginText();
                        contentStream.setFont(PDType1Font.HELVETICA, 10);
                        contentStream.newLineAtOffset(50, 730);
                        y = 730;
                    }
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText(wrapped);
                    y -= 15;
                }
            }

            contentStream.endText();
            contentStream.close();
            document.save(target.toFile());
        }
        return target;
    }

    private Path writeCsv(Path target, String header, String[] rows) throws IOException {
        if (target.getParent() != null) {
            Files.createDirectories(target.getParent());
        }
        StringBuilder builder = new StringBuilder();
        builder.append(header).append(System.lineSeparator());
        for (String row : rows) {
            builder.append(row).append(System.lineSeparator());
        }
        Files.write(target, builder.toString().getBytes(StandardCharsets.UTF_8));
        return target;
    }

    private String row(Object... values) {
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < values.length; index++) {
            if (index > 0) {
                builder.append(',');
            }
            String value = values[index] == null ? "" : String.valueOf(values[index]);
            builder.append('"').append(value.replace("\"", "\"\"")).append('"');
        }
        return builder.toString();
    }

    private String sanitizePdfText(String text) {
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFKD);
        return normalized.replaceAll("[^\\x20-\\x7E]", " ");
    }

    private List<String> wrapLine(String text, int maxLength) {
        java.util.ArrayList<String> wrapped = new java.util.ArrayList<>();
        if (text == null || text.isEmpty()) {
            wrapped.add("");
            return wrapped;
        }

        String remaining = text;
        while (remaining.length() > maxLength) {
            int breakAt = remaining.lastIndexOf(' ', maxLength);
            if (breakAt <= 0) {
                breakAt = maxLength;
            }
            wrapped.add(remaining.substring(0, breakAt));
            remaining = remaining.substring(breakAt).trim();
        }
        wrapped.add(remaining);
        return wrapped;
    }
}