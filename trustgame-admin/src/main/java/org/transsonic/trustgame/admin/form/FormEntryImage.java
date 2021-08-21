package org.transsonic.trustgame.admin.form;

import org.jooq.Record;
import org.jooq.TableField;

public class FormEntryImage extends AbstractFormEntry<FormEntryImage, byte[]> {

    /** the picture itself is going to/from the database; the String codes for the filename. */
    String filename;

    /** the bytes of the file after reading. */
    byte[] image;

    /** the name of servlet to retrieve the current image; if null or empty: no current image. */
    private String imageServlet;

    /** the record number for retrieving the current image. */
    private int imageRecordNr;
    
    /** the image number to retrieve if multiple images exist (0 means do not query). */
    private int imageNr;
    
    /** large image (200x200). */
    private boolean largeImage;

    public FormEntryImage(TableField<?, byte[]> tableField) {
        super(tableField);
        this.filename = "";
        this.imageServlet = "";
        this.largeImage = false;
        this.imageNr = 0;
    }

    @Override
    public void validate(String s) {
        // Nothing to validate on the filename itself
    }

    @Override
    public String codeForEdit(byte[] image) {
        this.image = image;
        return this.filename;
    }

    @Override
    public byte[] codeForDatabase(String s) {
        this.filename = s;
        return this.image;
    }

    public byte[] getImage() {
        return this.image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getFilename() {
        return this.filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getImageServlet() {
        return imageServlet;
    }

    public FormEntryImage setImageServlet(String imageServlet) {
        this.imageServlet = imageServlet;
        return this;
    }

    public int getImageRecordNr() {
        return imageRecordNr;
    }

    public FormEntryImage setImageRecordNr(int imageRecordNr) {
        this.imageRecordNr = imageRecordNr;
        return this;
    }

    public boolean isLargeImage() {
        return largeImage;
    }

    public FormEntryImage setLargeImage(boolean largeImage) {
        this.largeImage = largeImage;
        return this;
    }

    public FormEntryImage setLargeImage() {
        this.largeImage = true;
        return this;
    }

    public int getImageNr() {
        return imageNr;
    }

    public FormEntryImage setImageNr(int imageNr) {
        this.imageNr = imageNr;
        return this;
    }

    @SuppressWarnings("unchecked")
    public String setRecordValue(Record record, byte[] value) {
        this.image = value;
        this.errors = "";
        try {
            record.set((TableField<?, byte[]>) getTableField(), image);
        } catch (Exception exception) {
            addError("Exception: " + exception.getMessage());
        }
        return this.errors;
    }

    @Override
    public String makeHtml() {
        StringBuilder s = new StringBuilder();
        s.append("    <tr>\n");
        s.append("      <td width=\"25%\">");
        s.append(getLabel());
        s.append("      </td>");
        s.append("      <td width=\"75%\">\n");
        s.append("        <input type=\"file\" accept=\"image/*\" onchange=\"previewImage(event, '");
        s.append(getTableField().getName());
        s.append("')\" name=\"");
        s.append(getTableField().getName());
        s.append("\" id=\"");
        s.append(getTableField().getName());
        if (isReadOnly())
            s.append("'\" readonly />\n");
        else
            s.append("'\" />\n");
        if (isLargeImage())
            s.append("        <div class=\"tg-preview-image-200\">\n");
        else
            s.append("        <div class=\"tg-preview-image-100\">\n");
        s.append("          <img id=\"");
        s.append(getTableField().getName());
        if (getImageServlet().length() > 0 && getImageRecordNr() > 0) {
            s.append("\" src=\"/trustgame-admin/");
            s.append(getImageServlet());
            s.append("?id=");
            s.append(getImageRecordNr());
            if (this.imageNr > 0) {
                s.append("&image=");
                s.append(getImageNr());
            }
        }
        s.append("\" />\n");
        s.append("        </div>\n");
        s.append("      </td>\n");
        s.append("    </tr>\n");
        return s.toString();
    }

}
