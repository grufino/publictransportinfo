import javax.inject.Inject
import play.api.OptionalDevContext
import play.api.http._
import play.api.routing.Router
import play.core.WebCommands

class RequestHandler @Inject()(webCommands: WebCommands,
                               optDevContext: OptionalDevContext,
                               router: Router,
                               errorHandler: HttpErrorHandler,
                               configuration: HttpConfiguration,
                               filters: HttpFilters)
    extends DefaultHttpRequestHandler(webCommands,
                                      optDevContext,
                                      router,
                                      errorHandler,
                                      configuration,
                                      filters)