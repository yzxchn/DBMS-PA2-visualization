ALTER TABLE lineitem DROP CONSTRAINT fk2;
ALTER TABLE orders DROP CONSTRAINT fk;
ALTER TABLE orders ADD CONSTRAINT fk_new FOREIGN KEY (o_custkey) REFERENCES customer ON DELETE CASCADE;
ALTER TABLE lineitem ADD CONStRAINT fk2_new FOREIGN KEY (l_orderkey) REFERENCES orders ON DELETE CASCADE;
