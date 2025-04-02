import { forwardRef, InputHTMLAttributes, ReactNode } from "react";
import styles from "./styles/Input.module.css";

type Props = {
  variant?: "filled" | "outline" | "line";
  placeholder?: string;
  className?: string;
  startElement?: ReactNode;
} & InputHTMLAttributes<HTMLInputElement>;

const Input = forwardRef<HTMLInputElement, Props>(
  (
    { variant = "filled", placeholder, className, startElement, ...props },
    ref,
  ) => {
    const defaultClassNames = styles["common"] + " " + styles[variant];
    return (
      <div className={styles["input-div"] + " " + className}>
        <input
          ref={ref}
          placeholder={placeholder}
          className={defaultClassNames}
          {...props}
        />
        <div className={styles["start-element"]}>{startElement}</div>
      </div>
    );
  },
);

Input.displayName = "Input";
export default Input;
