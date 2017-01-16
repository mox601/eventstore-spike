package fm.mox.spikes.eventstore.repositories.impl;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com.
 */
public class StreamNames {

    public static final String DASH = "-";

    public static final String CUSTOMER = "customer";
    public static final String ORDER = "order";

    public static final String CUSTOMER_DASH = CUSTOMER + DASH;
    public static final String ORDER_DASH = ORDER + DASH;

    public static final String CUSTOMERS_CATEGORY = "$ce-" + StreamNames.CUSTOMER;
    public static final String ORDERS_CATEGORY = "$ce-" + StreamNames.ORDER;
}
