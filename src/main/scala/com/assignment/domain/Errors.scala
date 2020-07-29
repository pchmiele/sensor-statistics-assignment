package com.assignment.domain

sealed trait Errors extends Throwable

final case object NoArgs extends Errors