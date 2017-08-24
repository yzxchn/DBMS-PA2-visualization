ALTER TABLE customer ADD PRIMARY KEY (c_custkey);
ALTER TABLE lineitem ADD PRIMARY KEY (l_orderkey, l_linenumber);
ALTER TABLE nation ADD PRIMARY KEY (n_nationkey);
ALTER TABLE orders ADD PRIMARY KEY (o_orderkey);
ALTER TABLE part ADD PRIMARY KEY (p_partkey);
ALTER TABLE partsupp ADD PRIMARY KEY (ps_partkey, ps_suppkey);
ALTER TABLE region ADD PRIMARY KEY (r_regionkey);
ALTER TABLE supplier ADD PRIMARY KEY (s_suppkey);
