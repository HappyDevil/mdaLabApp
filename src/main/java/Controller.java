import au.com.ds.ef.EasyFlow;
import au.com.ds.ef.call.ContextHandler;

import static au.com.ds.ef.FlowBuilder.from;
import static au.com.ds.ef.FlowBuilder.on;
import static enums.Events.*;
import static enums.States.*;

public class Controller {

    public Controller() {

        EasyFlow<FlowContext> flow =
                from(SHOWING_WELCOME).transit(
                        on(cardPresent).to(WAITING_FOR_PIN).transit(

                        ),
                        on(cancel).to(RETURNING_CARD).transit(
                                on(cardExtracted).to(SHOWING_WELCOME)
                        )
                );

        flow.whenEnter(SHOWING_WELCOME, new ContextHandler<FlowContext>() {
            public void call(FlowContext flowContext) throws Exception {

            }
        });

        flow.start(new FlowContext());
    }
}
