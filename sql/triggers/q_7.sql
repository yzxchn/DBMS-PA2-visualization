CREATE TABLE move_rate (
    from_region_key integer REFERENCES region,
    to_region_key   integer REFERENCES region,
    rate_change numeric,
    PRIMARY KEY (from_region_key, to_region_key)
);

INSERT INTO move_rate 
VALUES
    (1, 1, 0),
    (1, 2, -0.2),
    (1, 3, 0.05),
    (2, 1, 0.2),
    (2, 2, 0),
    (2, 3, 0.1),
    (3, 1, -0.05),
    (3, 2, -0.1),
    (3, 3, 0);

CREATE OR REPLACE FUNCTION update_cost_on_move() RETURNS TRIGGER AS $$
    DECLARE
        new_region_key integer;
        old_region_key integer;
        rate_change numeric;
    BEGIN
        IF NEW.s_nationkey <> OLD.s_nationkey THEN
            SELECT INTO new_region_key n_regionkey
            FROM nation 
            WHERE n_nationkey = NEW.s_nationkey;
            
            SELECT INTO old_region_key n_regionkey
            FROM nation 
            WHERE n_nationkey = OLD.s_nationkey;
            
            IF new_region_key <> old_region_key THEN
                SELECT INTO rate_change move_rate.rate_change
                FROM move_rate
                WHERE from_region_key = old_region_key AND
                      to_region_key = new_region_key;
                UPDATE partsupp
                SET ps_supplycost = ps_supplycost*(1+rate_change)
                WHERE ps_suppkey = NEW.s_suppkey;
            END IF;
        END IF;
        RETURN NEW;
    END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER cost_update_on_move AFTER UPDATE
    ON supplier
    FOR EACH ROW
    EXECUTE PROCEDURE update_cost_on_move();
