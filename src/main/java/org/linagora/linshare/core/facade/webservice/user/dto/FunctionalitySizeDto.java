/*
 * LinShare is an open source filesharing software developed by LINAGORA.
 * 
 * Copyright (C) 2015-2021 LINAGORA
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version, provided you comply with the Additional Terms applicable for
 * LinShare software by Linagora pursuant to Section 7 of the GNU Affero General
 * Public License, subsections (b), (c), and (e), pursuant to which you must
 * notably (i) retain the display in the interface of the “LinShare™”
 * trademark/logo, the "Libre & Free" mention, the words “You are using the Free
 * and Open Source version of LinShare™, powered by Linagora © 2009–2021.
 * Contribute to Linshare R&D by subscribing to an Enterprise offer!”. You must
 * also retain the latter notice in all asynchronous messages such as e-mails
 * sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain
 * from infringing Linagora intellectual property rights over its trademarks and
 * commercial brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for more
 * details.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License and
 * its applicable Additional Terms for LinShare along with this program. If not,
 * see <http://www.gnu.org/licenses/> for the GNU Affero General Public License
 * version 3 and
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for the
 * Additional Terms applicable to LinShare software.
 */
package org.linagora.linshare.core.facade.webservice.user.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.linagora.linshare.core.domain.constants.FileSizeUnit;

@XmlRootElement(name = "FunctionalitySize")
public class FunctionalitySizeDto extends FunctionalityDto {

	protected Integer value;

	protected Integer maxValue;

	protected String unit;

	protected String maxUnit;

	protected List<String> units = new ArrayList<String>();

	public FunctionalitySizeDto(FileSizeUnit... units) {
		super();
		for (FileSizeUnit sizeUnit : units) {
			this.units.add(sizeUnit.toString());
		}
	}

	public FunctionalitySizeDto() {
		super();
		this.units.add(FileSizeUnit.KILO.toString());
		this.units.add(FileSizeUnit.MEGA.toString());
		this.units.add(FileSizeUnit.GIGA.toString());
	}

	public FunctionalitySizeDto(Integer value, String unit, List<String> units) {
		super();
		this.value = value;
		this.unit = unit;
		this.units = units;
	}

	public Integer getValue() {
		return value;
	}

	@XmlElement
	public Long getRawSize() {
		if (getUnit() != null) {
			FileSizeUnit unit = FileSizeUnit.valueOf(getUnit());
			return unit.getSiSize(value);
		}
		return 0L;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getMaxUnit() {
		return maxUnit;
	}

	public void setMaxUnit(String maxUnit) {
		this.maxUnit = maxUnit;
	}

	public List<String> getUnits() {
		return units;
	}

	public void setUnits(List<String> units) {
		this.units = units;
	}

	public Integer getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(Integer defaultValue) {
		this.maxValue = defaultValue;
	}

}
