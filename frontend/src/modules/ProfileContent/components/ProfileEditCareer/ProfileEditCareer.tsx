import { useState } from "react";
import { Input, Select, PageTitle, Divider, Button } from "@/ui";
import { FormField } from "@/components";

interface CareerData {
  workplace: string;
  startYear: string;
  endYear: string;
  position: string;
}

interface ProfileEditCareerProps {
  career?: CareerData;
  onSave: (career: CareerData) => void;
  onBack: () => void;
}

const defaultCareer: CareerData = {
  workplace: "",
  startYear: "",
  endYear: "",
  position: "",
};

// Generate years from current year back to 1950
const years = Array.from({ length: 76 }, (_, i) => String(2026 - i));

export default function ProfileEditCareer({
  career = defaultCareer,
  onSave,
  onBack,
}: ProfileEditCareerProps) {
  const [formData, setFormData] = useState<CareerData>(career);

  const handleChange = (field: keyof CareerData, value: string) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
  };

  const handleSave = () => {
    onSave(formData);
  };

  return (
    <div
      className="bg-[#FEFEFE] overflow-hidden w-full"
      style={{
        boxShadow: "0px 2px 8px rgba(0, 0, 0, 0.25)",
        maxWidth: "975px",
        borderRadius: "24px",
        height: "504px",
        position: "relative",
      }}
    >
      {/* Title */}
      <div
        className="absolute"
        style={{ left: "50%", transform: "translateX(-50%)", top: "26px" }}
      >
        <PageTitle>Карьера</PageTitle>
      </div>

      {/* Divider 1 */}
      <div className="absolute" style={{ width: "935px", left: "20px", top: "113px" }}>
        <Divider />
      </div>

      {/* Form Fields */}
      <div
        className="absolute flex flex-col justify-center items-center"
        style={{
          width: "935px",
          height: "292px",
          left: "20px",
          top: "114px",
          padding: "39px 0",
        }}
      >
        <div
          className="flex flex-col justify-center items-end gap-[18px]"
          style={{ width: "935px", paddingRight: "263px" }}
        >
          {/* Workplace */}
          <FormField label="Место работы:">
            <Input
              variant="profile"
              type="text"
              value={formData.workplace}
              onChange={(e) => handleChange("workplace", e.target.value)}
              placeholder="Укажите компанию"
              className="w-[326px]"
            />
          </FormField>

          {/* Start Year */}
          <FormField label="Год начала работы:">
            <Select
              variant="profile"
              value={formData.startYear}
              onChange={(e) => handleChange("startYear", e.target.value)}
              className="w-[326px]"
            >
              <option value="">Не выбран</option>
              {years.map((year) => (
                <option key={year} value={year}>
                  {year}
                </option>
              ))}
            </Select>
          </FormField>

          {/* End Year */}
          <FormField label="Год окончания работы:">
            <Select
              variant="profile"
              value={formData.endYear}
              onChange={(e) => handleChange("endYear", e.target.value)}
              className="w-[326px]"
            >
              <option value="">Не выбран</option>
              {years.map((year) => (
                <option key={year} value={year}>
                  {year}
                </option>
              ))}
            </Select>
          </FormField>

          {/* Position */}
          <FormField label="Должность:">
            <Input
              variant="profile"
              type="text"
              value={formData.position}
              onChange={(e) => handleChange("position", e.target.value)}
              className="w-[326px]"
            />
          </FormField>
        </div>
      </div>

      {/* Divider 2 */}
      <div className="absolute" style={{ width: "935px", left: "20px", top: "406px" }}>
        <Divider />
      </div>

      {/* Save Button */}
      <div
        className="absolute"
        style={{ left: "50%", transform: "translateX(-50%)", top: "433px" }}
      >
        <Button
          variant="primary"
          onClick={handleSave}
          className="w-[100px] h-[45px] rounded-lg text-[15px]"
        >
          Сохранить
        </Button>
      </div>
    </div>
  );
}
