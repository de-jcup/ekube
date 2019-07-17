package de.jcup.ekube.explorer;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import de.jcup.ekube.core.KeyValueMap;

public class ShowBase64EncodedSecretDataDialog extends TitleAreaDialog {

    private StyledText txtInfo;

    private String info;

    private StyleRange[] ranges;

    public ShowBase64EncodedSecretDataDialog(Shell parentShell) {
        super(parentShell);
    }

    @Override
    public void create() {
        super.create();
        setTitle("Secret base64 decoded");
        setMessage("Information about base64 encoded secret data", IMessageProvider.INFORMATION);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);
        Composite container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout layout = new GridLayout(1, false);
        container.setLayout(layout);

        createInfoTextField(container);

        return area;
    }
    private void createInfoTextField(Composite container) {
        GridData data = new GridData();
        data.grabExcessVerticalSpace=true;
        data.verticalAlignment=GridData.FILL;
        
        data.grabExcessHorizontalSpace = true;
        data.horizontalAlignment = GridData.FILL;

     // retrieve the font preference from the theme manager
        
        txtInfo = new StyledText(container, SWT.BORDER|SWT.MULTI|SWT.WRAP| SWT.H_SCROLL|SWT.V_SCROLL);
        txtInfo.setFont( JFaceResources.getTextFont());
        txtInfo.setMargins(10, 10, 10, 10);
        txtInfo.setLayoutData(data);
        txtInfo.setText(info);
        txtInfo.setEditable(false);
        
        txtInfo.setStyleRanges(ranges);
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    public void setKeyValueMap(KeyValueMap keyValueMap) {
        StringBuilder sb = new StringBuilder();
        List<StyleRange> rangeList = new ArrayList<>();
       
        Color colorForeGround = JFaceResources.getColorRegistry().get(JFacePreferences.ACTIVE_HYPERLINK_COLOR);
        Color colorBackground = null;
        
        for (String key: keyValueMap.keySet()){
            String data = keyValueMap.get(key);
            if (data==null){
                continue;
            }
            String base64decoded;
            try{
                byte[] decoded = Base64.getDecoder().decode(data);
                base64decoded=new String(decoded);
                if (base64decoded.trim().length()!=base64decoded.length()){
                    setErrorMessage("Found trailing/leading whitespaces at decoded value of "+key);
                }
            }catch(Exception e){
                base64decoded = "FAILED:"+e.getMessage();
            }
            
            sb.append(  "Key            : \"");
            int start1=sb.length();
            sb.append(key);
           
            int length1 = sb.length()-start1;
            StyleRange range = new StyleRange();
            range.start=start1;
            range.length=length1;
            range.foreground=colorForeGround;
            range.background=colorBackground;
            rangeList.add(range);
            
            sb.append('"');;
            sb.append("\n");
            
            sb.append("\nValue          : '").append(data).append('\'');
            sb.append("\nBase64 decoded : '");
            int start2=sb.length();
            sb.append(base64decoded);
            int length2 = sb.length()-start2;
            range = new StyleRange();
            range.start=start2;
            range.length=length2;
            range.background=colorBackground;
            range.foreground=colorForeGround;
            rangeList.add(range);
            
            sb.append('\'');
            sb.append("\n\n\n\n");
        }
        ranges= rangeList.toArray(new StyleRange[rangeList.size()]);
        info=sb.toString();
    }
}
