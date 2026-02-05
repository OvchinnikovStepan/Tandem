import { useState } from "react";
import { Camera, Calendar } from "lucide-react";
import { Input, Select, Textarea, PageTitle, Divider, Button, Avatar } from "@/ui";
import { FormField } from "@/components";

interface UserProfile {
  id: string;
  firstName: string;
  lastName: string;
  username: string;
  gender: string;
  birthDate: string;
  bio: string;
  city: string;
  avatar?: string;
}

interface ProfileEditProps {
  user: UserProfile;
  onSave: (user: UserProfile) => void;
  onCancel: () => void;
}

export default function ProfileEdit({ user, onSave, onCancel }: ProfileEditProps) {
  const [formData, setFormData] = useState<UserProfile>(user);

  const handleChange = (field: keyof UserProfile, value: string) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onSave(formData);
  };

  return (
    <div
      className="bg-[#FEFEFE] overflow-hidden w-full"
      style={{
        boxShadow: "0px 2px 8px rgba(0, 0, 0, 0.25)",
        maxWidth: "975px",
        borderRadius: "24px",
        height: "763px",
        position: "relative",
      }}
    >
      <div
        className="absolute"
        style={{ left: "50%", transform: "translateX(-50%)", top: "26px" }}
      >
        <PageTitle>Профиль</PageTitle>
      </div>
      <div
        className="absolute"
        style={{ width: "935px", height: "176px", left: "20px", top: "74px" }}
      >
        <div
          className="absolute cursor-pointer group"
          style={{
            left: "calc(50% - 100px/2 - 345.5px)",
            top: "38px",
          }}
        >
          <Avatar
            src={formData.avatar || "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200&h=200&fit=crop&crop=face"}
            alt="Avatar"
            size="xl"
          />
          <div className="absolute inset-0 bg-black/40 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity rounded-full">
            <Camera className="w-8 h-8 text-white" />
          </div>
        </div>

        <div
          className="absolute flex flex-col items-start gap-[18px]"
          style={{
            width: "410px",
            height: "98px",
            left: "calc(50% - 410px/2 - 0.5px)",
            top: "calc(50% - 98px/2)",
          }}
        >
          <FormField label="Имя:">
            <Input
              variant="profile"
              type="text"
              value={formData.firstName}
              onChange={(e) => handleChange("firstName", e.target.value)}
              className="w-[326px] font-bold"
            />
          </FormField>

          <FormField label="Фамилия:">
            <Input
              variant="profile"
              type="text"
              value={formData.lastName}
              onChange={(e) => handleChange("lastName", e.target.value)}
              className="w-[326px] font-bold"
            />
          </FormField>
        </div>
      </div>

      <div className="absolute" style={{ width: "935px", left: "20px", top: "250px" }}>
        <Divider />
      </div>

      <div
        className="absolute flex flex-col justify-center items-center"
        style={{
          width: "935px",
          height: "414px",
          left: "20px",
          top: "251px",
          padding: "39px 0",
          gap: "16px",
        }}
      >
        <div
          className="flex flex-col justify-center items-end gap-[18px]"
          style={{ width: "935px", paddingRight: "263px" }}
        >

          <FormField label="Пол:">
            <Select
              variant="profile"
              value={formData.gender}
              onChange={(e) => handleChange("gender", e.target.value)}
              className="w-[326px] font-bold"
            >
              <option value="male">Мужской</option>
              <option value="female">Женский</option>
              <option value="other">Другой</option>
            </Select>
          </FormField>

          <FormField label="Дата рождения:">
            <Input
              variant="profile"
              type="text"
              value={formData.birthDate}
              onChange={(e) => handleChange("birthDate", e.target.value)}
              placeholder="00 / 00 / 0000"
              icon={<Calendar className="w-5 h-5 text-[#333333]" />}
              className="w-[326px]"
            />
          </FormField>

          <FormField label="Имя пользователя:">
            <Input
              variant="profile"
              type="text"
              value={formData.username}
              onChange={(e) => handleChange("username", e.target.value)}
              className="w-[326px] font-bold"
            />
          </FormField>

          <FormField label="Краткая информация:" labelAlign="start">
            <Textarea
              variant="profile"
              value={formData.bio}
              onChange={(e) => handleChange("bio", e.target.value)}
              placeholder="Расскажите о себе..."
              className="w-[326px] h-[104px]"
            />
          </FormField>

          <FormField label="Ваш город:">
            <Input
              variant="profile"
              type="text"
              value={formData.city}
              onChange={(e) => handleChange("city", e.target.value)}
              className="w-[326px] font-bold"
            />
          </FormField>
        </div>
      </div>

      <div className="absolute" style={{ width: "935px", left: "20px", top: "665px" }}>
        <Divider />
      </div>

      <div
        className="absolute"
        style={{ left: "50%", transform: "translateX(-50%)", top: "692px" }}
      >
        <Button
          variant="primary"
          onClick={handleSubmit}
          className="w-[100px] h-[45px] rounded-lg text-[15px]"
        >
          Сохранить
        </Button>
      </div>
    </div>
  );
}
