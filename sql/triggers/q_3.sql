CREATE OR REPLACE FUNCTION check_order_count() RETURNS TRIGGER AS $$
    BEGIN
        IF (SELECT COUNT(o_orderkey) FROM orders WHERE o_custkey = NEW.o_custkey AND o_orderstatus = 'O') > 14 THEN
            RAISE EXCEPTION 'Can''t have more than 14 open orders per customer.';
        END IF;
        RETURN NEW;
    END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER order_count_constraint AFTER INSERT
    ON orders
    FOR EACH ROW
    EXECUTE PROCEDURE check_order_count();
