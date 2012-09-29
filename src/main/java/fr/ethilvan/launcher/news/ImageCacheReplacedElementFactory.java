package fr.ethilvan.launcher.news;

import org.w3c.dom.Element;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.extend.ReplacedElementFactory;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.FormSubmissionListener;
import org.xhtmlrenderer.swing.ImageReplacedElement;

public class ImageCacheReplacedElementFactory
        implements ReplacedElementFactory {

    private final ImageCache cache;
    private final ReplacedElementFactory delegate;

    public ImageCacheReplacedElementFactory(ImageCache cache,
            ReplacedElementFactory delegate) {
        this.cache = cache;
        this.delegate = delegate;
    }

    @Override
    public ReplacedElement createReplacedElement(LayoutContext context,
            BlockBox box, UserAgentCallback uac, int cssWidth, int cssHeight) {
        if (box.getElement().getNodeName().equals("img")) {
            String imgUrl = box.getElement().getAttribute("src");
            if (cache.contains(imgUrl)) {
                return new ImageReplacedElement(cache.get(imgUrl), cssWidth,
                        cssHeight);
            }
            return delegate.createReplacedElement(context, box, uac, cssWidth,
                    cssHeight);
        } else {
            return delegate.createReplacedElement(context, box, uac, cssWidth,
                    cssHeight);
        }
    }

    @Override
    public void reset() {
        delegate.reset();
    }

    @Override
    public void remove(Element element) {
        delegate.reset();
    }

    @Override
    public void setFormSubmissionListener(FormSubmissionListener listener) {
        delegate.setFormSubmissionListener(listener);
    }
}
