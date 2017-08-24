CREATE OR REPLACE FUNCTION update_order_status() RETURNS TRIGGER AS $$
    DECLARE
        unique_order_status integer;
    BEGIN
        SELECT INTO unique_order_status COUNT(l_linestatus) 
        FROM lineitem 
        WHERE l_orderkey = NEW.l_orderkey 
        GROUP BY l_linestatus;
        IF unique_order_status > 1 THEN
            UPDATE orders SET o_orderstatus = 'P' WHERE o_orderkey = NEW.l_orderkey;
        ELSE
            UPDATE orders SET o_orderstatus = NEW.l_linestatus WHERE o_orderkey = NEW.l_orderkey;
        END IF;
        RETURN NEW;
    END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER order_status_update AFTER INSERT
    ON lineitem
    FOR EACH ROW
    EXECUTE PROCEDURE update_order_status();
