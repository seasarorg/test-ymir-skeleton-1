package ${rootPackageName}.page;

import org.t2framework.commons.annotation.composite.RequestScope;
import org.t2framework.commons.util.Logger;
import org.t2framework.t2.annotation.core.Default;
import org.t2framework.t2.annotation.core.Page;
import org.t2framework.t2.contexts.WebContext;
import org.t2framework.t2.navigation.Forward;
import org.t2framework.t2.spi.Navigation;


@Page("index")
@RequestScope
public class IndexPage
{
    @SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(IndexPage.class);


    @Default
    public Navigation index(final WebContext context)
    {
        return Forward.to("/WEB-INF/pages/index.jsp");
    }
}
