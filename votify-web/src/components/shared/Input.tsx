import { forwardRef, InputHTMLAttributes, ReactNode } from "react";
import styles from "./styles/Input.module.css";

type Props = {
  variant?: "filled" | "outline" | "line";
  placeholder?: string;
  className?: string;
  startElement?: ReactNode;
  endElement?: ReactNode;
} & InputHTMLAttributes<HTMLInputElement>;

const Input = forwardRef<HTMLInputElement, Props>(
  (
    {
      variant = "filled",
      placeholder,
      className,
      startElement,
      endElement,
      ...props
    },
    ref,
  ) => {
    const defaultClassNames = styles["common"] + " " + styles[variant];
    const startElementClassName =
      startElement === undefined || startElement === null
        ? ""
        : styles["input-div-start-element"] + " ";
    const endElementClassName =
      endElement === undefined || endElement === null
        ? ""
        : styles["input-div-end-element"] + " ";
    return (
      <div
        className={
          styles["input-div"] +
          " " +
          startElementClassName +
          endElementClassName +
          className
        }
      >
        <input
          ref={ref}
          placeholder={placeholder}
          className={defaultClassNames}
          {...props}
        />
        <div className={styles["start-element"]}>{startElement}</div>
        <div className={styles["end-element"]}>{endElement}</div>
      </div>
    );
  },
);

Input.displayName = "Input";
export default Input;
