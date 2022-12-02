/**
 * The {@code Either} monad provides a means for modelling two types
 * simultaneously, acting as a binary summation type holding
 * concrete (non-null) object instances.
 * 
 * <h2>Why Use Either?</h2>
 * 
 * <p>The Either type offers a couple of compelling use cases.</p>
 * 
 * <ol type='1'>
 * <li>It can act as a generic return type holding equally valid values,
 * preventing the need to make specialized result classes.</li>
 * <li>It can act as a rich {@link java.util.Optional Optional} type, providing
 * contextual information for the empty case.</li>
 * <li>It can act as an alternative means of exception handling that allows
 * non-throwable signals and doesn't unwind the call stack.</li>
 * </ol>
 * 
 * <p>The third use case in particular can lead to much more compact and
 * readable code, with the additional ability to delay error handling
 * if needed. Compare the following invocation&hellip;</p>
 * 
 * <pre>
 * long id = ...;
 * Either.lift(DB::get).apply(id)
 *     .mapRight(Foo::getBar)
 *     .flatMapRight(Either.lift(Bar::toBaz))
 *     .consume(Exception::printStackTrace, Baz::show);
 * </pre>
 * 
 * <p>&hellip;with a more conventional try-catch solution.</p>
 * 
 * <pre>
 * long id = ...;
 * try {
 *     Bar bar = DB.get(id).getBar();
 *     try {
 *         bar.toBaz().show();
 *     } catch (Exception e) {
 *         e.printStackTrace();
 *     }
 * } catch (Exception e) {
 *     e.printStackTrace();
 * }
 * </pre>
 */
package lindelt.either;
