# Copyright (c) TIKI Inc.
# MIT license. See LICENSE file in root directory.

resource "digitalocean_database_cluster" "db-cluster-l0-storage" {
  name                 = "l0-storage-db-cluster-${local.region}"
  engine               = "pg"
  version              = "14"
  size                 = "db-s-1vcpu-1gb"
  region               = local.region
  node_count           = 1
  private_network_uuid = local.vpc_uuid
}

resource "digitalocean_database_db" "db-l0-storage" {
  cluster_id = digitalocean_database_cluster.db-cluster-l0-storage.id
  name       = "l0_storage"
}

resource "digitalocean_database_firewall" "db-cluster-l0-storage-fw" {
  cluster_id = digitalocean_database_cluster.db-cluster-l0-storage.id

  rule {
    type  = "app"
    value = digitalocean_app.l0-storage-app.id
  }
}

resource "digitalocean_database_user" "db-user-l0-storage" {
  cluster_id = digitalocean_database_cluster.db-cluster-l0-storage.id
  name       = "l0-storage-service"
}