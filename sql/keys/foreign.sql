ALTER TABLE customer ADD CONSTRAINT fk FOREIGN KEY (c_nationkey) REFERENCES nation;
ALTER TABLE lineitem ADD CONSTRAINT fk1 FOREIGN KEY (l_partkey, l_suppkey) REFERENCES partsupp;
ALTER TABLE lineitem ADD CONSTRAINT fk2 FOREIGN KEY (l_orderkey) REFERENCES orders;
ALTER TABLE nation ADD CONSTRAINT fk FOREIGN KEY (n_regionkey) REFERENCES region;
ALTER TABLE orders ADD CONSTRAINT fk FOREIGN KEY (o_custkey) REFERENCES customer;
ALTER TABLE partsupp ADD CONSTRAINT fk1 FOREIGN KEY (ps_suppkey) REFERENCES supplier;
ALTER TABLE partsupp ADD CONSTRAINT fk2 FOREIGN KEY (ps_partkey) REFERENCES part;
ALTER TABLE supplier ADD CONSTRAINT fk FOREIGN KEY (s_nationkey) REFERENCES nation;
