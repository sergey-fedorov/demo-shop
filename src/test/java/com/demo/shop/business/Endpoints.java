package com.demo.shop.business;

public class Endpoints {

    public static class Orders {
        private static final String BASE_URI = "/api/orders";

        public static final String ORDERS = BASE_URI;
        public static final String ORDER_BY_ID = BASE_URI + "/{id}";
        public static final String STATUS = BASE_URI + "/status";
        public static final String PAY = BASE_URI + "/pay";
    }

    public static class OrderItems {
        private static final String BASE_URI = "/api/order-items";

        public static final String ORDER_ITEMS = BASE_URI;
    }

    public static class Products {
        private static final String BASE_URI = "/api/products";

        public static final String PRODUCTS = BASE_URI;
        public static final String PRODUCT_BY_ID = BASE_URI + "/{id}";
    }

    public static class Customers {
        private static final String BASE_URI = "/api/customers";

        public static final String CUSTOMERS = BASE_URI;
        public static final String CUSTOMER_BY_ID = BASE_URI + "/{id}";
    }

    /* Third-party services */

    public static class Delivery {
        private static final String BASE_URI = "/api/delivery";

        public static final String PAID_ORDERS = BASE_URI + "/list";
        public static final String COMPLETE = BASE_URI + "/complete";
    }

    public static class Payment {
        private static final String BASE_URI = "/api/payment";

        public static final String PROCEED = BASE_URI + "/proceed";
    }
}
