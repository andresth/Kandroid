package in.andres.kandroid;

import org.commonmark.node.Paragraph;
import org.commonmark.renderer.html.CoreHtmlNodeRenderer;
import org.commonmark.renderer.html.HtmlNodeRendererContext;
import org.commonmark.renderer.html.HtmlWriter;

/**
 * Created by thomas on 05.02.17.
 */

public class CompactHtmlRenderer extends CoreHtmlNodeRenderer {
    private final HtmlWriter html;

    CompactHtmlRenderer(HtmlNodeRendererContext context) {
        super(context);
        this.html = context.getWriter();
    }

    @Override
    public void visit(Paragraph node) {
        // Replace paragraphs with line breaks to get a compact view.
        if (node.getNext() == null) {
            html.line();
            visitChildren(node);
            html.line();
        } else {
            super.visit(node);
        }
    }

}
