CREATE OR REPLACE FUNCTION update_part_price() RETURNS trigger AS $$
DECLARE
    price_change numeric(3);
BEGIN
    IF NEW.p_retailprice <> OLD.p_retailprice THEN
            price_change := NEW.p_retailprice-OLD.p_retailprice;
            UPDATE partsupp SET ps_supplycost = ps_supplycost + price_change
            WHERE ps_partkey = NEW.p_partkey;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER p_part_update 
AFTER UPDATE ON part
FOR EACH ROW 
EXECUTE PROCEDURE update_part_price();
