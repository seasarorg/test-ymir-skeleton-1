package ${rootPackageName}.page;

import org.t2framework.annotation.core.Default;
import org.t2framework.annotation.core.Page;
import org.t2framework.contexts.WebContext;
import org.t2framework.navigation.Forward;
import org.t2framework.spi.Navigation;

import commons.annotation.composite.RequestScope;
import commons.util.Logger;


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
