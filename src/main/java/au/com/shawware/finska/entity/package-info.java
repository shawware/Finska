/*
 * Copyright (C) 2018 shawware.com.au License: GNU General Public License V3 (or
 * later) http://www.gnu.org/copyleft/gpl.html
 */

/**
 * The domain model entities and value objects.
 * 
 * Entities that contain other entities, eg. a {@link au.com.shawware.finska.entity.Match}
 * is made up of {@link au.com.shawware.finska.entity.Game}s, maintains this
 * relationship via composition and IDs. This is to aid conversion to JSON
 * without duplication of data. That is, the IDs of the entities are used,
 * rather than the entities themselves.
 */
package au.com.shawware.finska.entity;